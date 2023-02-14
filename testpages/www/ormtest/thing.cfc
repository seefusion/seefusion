component persistent="true" table="things" {

	property name="id" column="thingid" fieldtype="id" generator="increment";
	property name="name" column="thingName" ormtype="string";     
	property name="description" ormtype="text";
	property name="price" ormtype="double";
	property name="isSold" ormtype="boolean";  

	function getName() {
		return uCase( variables.name );
	}

	function getProfit() {
		if ( getIsSold() ) {
			return getPrice() * 0.2;
		} else {
			return 0;
		}
	}

}