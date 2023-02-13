package com.seefusion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipContainingJarsClassLoader extends ClassLoader {

	private static Logger LOG = Logger.getLogger(ZipContainingJarsClassLoader.class.getName());
	
	static {
		try {
			URL.setURLStreamHandlerFactory(new SeeFusionUrlStreamHandlerFactory());
		}
		catch(Error e) {
			LOG.warning("Unable to register SeeFusionUrlStreamHandlerFactory: " + e.getMessage());
		}
	}

	static HashMap<String, Location> locations = new HashMap<>();
	static HashMap<String, byte[]> entries = new HashMap<>();
	static HashMap<String, Class<?>> classes = new HashMap<>();

	static class Location {
		String jarFile;
		String entry;
		Location(String jarFile, String entry) {
			this.jarFile = jarFile;
			this.entry = entry;
		}
	}

	private static File sdkLoc;
	
	ZipContainingJarsClassLoader(ClassLoader classLoader, File zip) throws IOException {
		super(classLoader);
		scanZip(zip);
	}

	synchronized void scanZip(File sdkLoc) throws IOException {
		ZipContainingJarsClassLoader.sdkLoc = sdkLoc;
		try (ZipFile in = new ZipFile(sdkLoc)) {
			ZipEntry entry;
			Enumeration<? extends ZipEntry> entries = in.entries();
			while(entries.hasMoreElements()) {
				entry = entries.nextElement();
				String name = entry.getName();
				if(!name.contains("documentation") && name.endsWith(".jar") && !name.endsWith("-javadoc.jar") && !name.endsWith("-sources.jar")) {
					scanJar(name, new ZipInputStream(in.getInputStream(entry)));
				}
			}
		}
	}

	long scanJar(String jarName, ZipInputStream in) throws IOException {
		ZipEntry entry;
		long totalSize = 0;
		while ((entry = in.getNextEntry()) != null) {
			String entryName = entry.getName();
			String name = entryName;
			if(name.endsWith("/") || name.endsWith(".html") || name.endsWith(".java") || name.startsWith("META-INF/")
					|| entry.isDirectory()) {
				continue;
			}
			if(name.endsWith(".class")) {
				name = name.replace('/', '.');
				name = name.substring(0, name.length() - ".class".length());
				if(name.startsWith("com.fasterxml.jackson.databind")
						|| name.startsWith("com.fasterxml.jackson.core")
						|| name.contains("cloudwatch")
				) {
					byte[] buf = readFully(in);
					entries.put(name, buf);
					continue;
				}
			}
			locations.put(name, new Location(jarName, entryName));
		}
		return totalSize;
	}

	static byte[] readFully(InputStream in) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = in.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();

		return buffer.toByteArray();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if(classes.containsKey(name)) {
			return classes.get(name);
		}
		if(entries.containsKey(name)) {
			byte[] buf = entries.get(name);
			return super.defineClass(name, buf, 0, buf.length);
		}
		if(locations.containsKey(name)) {
			byte[] buf;
			try {
				buf = loadLocation(name, locations.get(name));
				Class<?> clazz = super.defineClass(name, buf, 0, buf.length);
				classes.put(name, clazz);
				return clazz;
			}
			catch (IOException e) {
				LOG.warning("Could not read seefusion-aws jar: " + e.getMessage());
				return null;
			}
		}
		else {
			throw new ClassNotFoundException(name);
		}
	}

	private static byte[] loadLocation(String name, Location location) throws IOException {
		System.out.println("Loading: " +  name);
		try (ZipFile zipFile = new ZipFile(sdkLoc)) {
			ZipEntry zipEntry = zipFile.getEntry(location.jarFile);
			ZipInputStream in = null;
			try {
				in = new ZipInputStream(zipFile.getInputStream(zipEntry));
				ZipEntry entry;
				while( (entry=in.getNextEntry()) != null ) {
					if(entry.getName().equals(location.entry)) {
						break;
					}
				}
				byte[] bytes = readFully(in);
				entries.put(name, bytes);
				return bytes;
			}
			finally {
				if(in != null) in.close();
			}
		}
	}
	
	ZipEntry find(ZipInputStream in, String name) throws IOException {
		ZipEntry entry;
		while( (entry = in.getNextEntry()) != null) {
			if(entry.getName().equals(name)) {
				return entry;
			}
		}
		return null;
	}

	@Override
	protected URL findResource(String name) {
		if(entries.containsKey(name)) {
			try {
				return new URL("seefusion:" + name);
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		return new Enumeration<URL>() {

			@Override
			public boolean hasMoreElements() {
				return false;
			}

			@Override
			public URL nextElement() {
				return null;
			}

		};

	}

	@Override
	public InputStream getResourceAsStream(String name) {
		if(entries.containsKey(name)) {
			return new ByteArrayInputStream(entries.get(name));
		}
		else if(locations.containsKey(name)) {
			try {
				return new ByteArrayInputStream(loadLocation(name, locations.get(name)));
			}
			catch (IOException e) {
				LOG.warning("Could not read seefusion-aws jar: " + e.getMessage());
				return null;
			}
		}
		return null;
	}

	public URLConnection openConnection(URL u) {
		return new SeeFusionUrlConnection(u);
	}

	public static byte[] getFile(String name) throws IOException {
		if(entries.containsKey(name)) {
			return entries.get(name);
		}
		if(locations.containsKey(name)) {
			return loadLocation(name, locations.get(name));
		}
		return null;
	}

}
