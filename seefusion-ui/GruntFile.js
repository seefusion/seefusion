module.exports = function (grunt) {

	grunt.initConfig({
		distdir: 'dist',
		pkg: grunt.file.readJSON('package.json'),
		banner:
			'/*! <%= pkg.title || pkg.name %> - v<%= pkg.version %> - <%= grunt.template.today("yyyy-mm-dd") %>\n' +
			'<%= pkg.homepage ? " * " + pkg.homepage + "\\n" : "" %>' +
			' * Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author %>;\n' +
			' * Licensed <%= _.map(pkg.licenses, "type").join(", ") %>\n */\n',
		src: {
			js: ['src/**/*.js'],
			jsTpl: ['<%= distdir %>/templates/**/*.js'],
			specs: ['test/**/*.spec.js'],
			scenarios: ['test/**/*.scenario.js'],
			html: ['src/index.html'],
			tpl: {
				app: ['src/app/**/*.tpl.html'],
				common: ['src/common/**/*.tpl.html']
			},
			less: ['src/less/stylesheet.less'], // recess:build doesn't accept ** in its file patterns
			lessWatch: ['src/less/**/*.less']
		},

		clean: {
			// Delete all files (not folders) in the distdir that are not .svn
			options: {
				force: true
			},
			dist:{
				src:['<%= distdir %>/**/*.*','!<%= distdir %>/**/.svn'],
			}
		},

		copy: {
			assets: {
				files: [
					{ dest: '<%= distdir %>', src : '**', expand: true, cwd: 'src/assets/' },
					{ dest: '<%= distdir %>', src : '**', expand: true, cwd: 'src/css/' },
					{ dest: '<%= distdir %>/nv.d3.min.css', src : 'bower_components/nvd3/nv.d3.min.css' },
					{ dest: '<%= distdir %>/angular.min.js.map', src : 'vendor/angular.min.js.map' },
					{ dest: '<%= distdir %>/jquery.min.map', src : 'vendor/jquery-2.1.1.min.map' },
					{ dest: '<%= distdir %>/angular-toggle-switch.css', src : 'bower_components/angular-toggle-switch/angular-toggle-switch-bootstrap-3.css'}
				]
			},
			glyphs:{
				files:[
					{dest:'dist/fonts/glyphicons-regular.eot',src:'vendor/glyphicons_pro/glyphicons/web/html_css/fonts/glyphicons-regular.eot'},
					{dest:'dist/fonts/glyphicons-regular.svg',src:'vendor/glyphicons_pro/glyphicons/web/html_css/fonts/glyphicons-regular.svg'},
					{dest:'dist/fonts/glyphicons-regular.ttf',src:'vendor/glyphicons_pro/glyphicons/web/html_css/fonts/glyphicons-regular.ttf'},
					{dest:'dist/fonts/glyphicons-regular.woff',src:'vendor/glyphicons_pro/glyphicons/web/html_css/fonts/glyphicons-regular.woff'}
				]
			},
		},

		karma: {
			//unit: { options: karmaConfig('test/config/unit.js') },
			//watch: { options: karmaConfig('test/config/unit.js', { singleRun:false, autoWatch: true}) }
		},

		html2js: {
			app: {
				options: {
					base: 'src/app'
				},
				src: ['<%= src.tpl.app %>'],
				dest: '<%= distdir %>/templates/app.js',
				module: 'templates.app'
			},
			common: {
				options: {
					base: 'src/common'
				},
				src: ['<%= src.tpl.common %>'],
				dest: '<%= distdir %>/templates/common.js',
				module: 'templates.common'
			}
		},

		concat:{
			dist:{
				options: {
					banner: "<%= banner %>"
				},
				src:['<%= src.js %>', '<%= src.jsTpl %>'],
				dest:'<%= distdir %>/<%= pkg.name %>.js'
			},
			index: {
				src: ['src/index.html'],
				dest: '<%= distdir %>/index.html',
				options: {
					process: true
				}
			},
			angular: {
				src:[
						'bower_components/angular/angular.min.js',
						'vendor/angular-bootstrap/ui-bootstrap-tpls-0.12.0-SNAPSHOT.min.js',
						'bower_components/angular-ui-router/release/angular-ui-router.min.js',
						'bower_components/checklist-model/checklist-model.js',
						'bower_components/angular-cookies/angular-cookies.min.js',
						'bower_components/d3/d3.min.js',
						'bower_components/nvd3/nv.d3.min.js',
						'bower_components/angularjs-nvd3-directives/dist/angularjs-nvd3-directives.min.js',
						'bower_components/angular-toggle-switch/angular-toggle-switch.min.js'
					],
				dest: '<%= distdir %>/angular.js'
			},
			jquery: {
				src:[
						'bower_components/jquery/dist/jquery.min.js'
					],
				dest: '<%= distdir %>/jquery.js'
			},
		},

		uglify: {
			dist:{
				options: {
					banner: "<%= banner %>"
				},
				src:['<%= src.js %>' ,'<%= src.jsTpl %>'],
				dest:'<%= distdir %>/<%= pkg.name %>.js'
			},
			angular: {
				src:['<%= concat.angular.src %>'],
				dest: '<%= distdir %>/angular.js'
			},
			jquery: {
				src:['<%= concat.jquery.src %>'],
				dest: '<%= distdir %>/jquery.js'
			}
		},

		less: {
			compileCore: {
				options: {
					strictMath: true,
					sourceMap: true,
					outputSourceFiles: true,
					sourceMapURL: '<%= pkg.name %>.css.map',
					sourceMapFilename: 'dist/<%= pkg.name %>.css.map'
				},
				files: {
					'dist/<%= pkg.name %>.css': '<%= src.less %>'
				}
			},
			compileTheme: {
				options: {
					strictMath: true,
					sourceMap: true,
					outputSourceFiles: true,
					sourceMapURL: '<%= pkg.name %>-theme.css.map',
					sourceMapFilename: 'dist/<%= pkg.name %>-theme.css.map'
				},
				files: {
					'dist/<%= pkg.name %>-theme.css': 'bower_components/bootstrap/less/theme.less'
				}
			},
			minify: {
				options: {
					cleancss: true,
					report: 'min'
				},
				files: {
					'dist/<%= pkg.name %>.min.css': 'dist/<%= pkg.name %>.css',
					'dist/<%= pkg.name %>-theme.min.css': 'dist/<%= pkg.name %>-theme.css',
					'dist/<%= pkg.name %>-author.min.css': 'dist/author.css'
				}
			}
		},


		jshint:{
			files:['gruntFile.js', '<%= src.js %>', '<%= src.jsTpl %>', '<%= src.specs %>', '<%= src.scenarios %>'],
			options:{
				curly:true,
				eqeqeq:true,
				immed:true,
				latedef:true,
				newcap:true,
				noarg:true,
				sub:true,
				boss:true,
				eqnull:true,
				globals:{}
			}
		},

		watch:{
			build: {
				files:['<%= src.js %>', '<%= src.specs %>', '<%= src.lessWatch %>', '<%= src.tpl.app %>', '<%= src.tpl.common %>', '<%= src.html %>'],
				tasks:['build','timestamp']
			}
		}
	});




	grunt.registerTask("default", function(){
		grunt.log.writeln(grunt.config.get('banner'));
	});
	grunt.registerTask('build', ['clean:dist','jshint','html2js','concat','copy','less']);

	grunt.registerTask('test', ['karma:unit']);

	// Print a timestamp (useful for when watching)
	grunt.registerTask('timestamp', function() {
		grunt.log.subhead(Date());
	});

	grunt.registerTask('dist-css', ['less:compileCore']);

	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-clean');
	grunt.loadNpmTasks('grunt-contrib-copy');
	grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.loadNpmTasks('grunt-contrib-less');
	grunt.loadNpmTasks('grunt-karma');
	grunt.loadNpmTasks('grunt-html2js');


	require('load-grunt-tasks')(grunt, {scope: 'devDependencies'});

	var karmaConfig = function(configFile, customOptions) {
		var options = { configFile: configFile, keepalive: true };
		var travisOptions = process.env.TRAVIS && { browsers: ['Firefox'], reporters: 'dots' };
		return grunt.util._.extend(options, customOptions, travisOptions);
	};

};
