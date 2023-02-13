package com.seefusion;


public class JsonDosSaveConfig extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker) {
		SeeFusion sf = talker.getSeeFusion();
		DosManager m = sf.getDosManager();
		m.setConfigJson(talker.getPostData());
		return JsonDosGetConfig._doJson(sf);
	}

	static Perm perm = new Perm(Perm.CONFIG);
	@Override
	Perm getPerm() {
		return perm;
	}


}
