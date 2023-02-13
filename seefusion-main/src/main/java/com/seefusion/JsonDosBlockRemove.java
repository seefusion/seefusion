package com.seefusion;

public class JsonDosBlockRemove extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker) {
		SeeFusion sf = talker.getSeeFusion();
		DosManager m = sf.getDosManager();
		String ip = talker.getPostData().get("ip").toString();
		m.removeBlock(ip);
		return JsonDosGetStatus._doJson(sf);
	}

	static Perm perm = new Perm(Perm.CONFIG);
	@Override
	Perm getPerm() {
		return perm;
	}


}
