package corelan.store;

public class JSONStoreMapping {

	public java.util.Hashtable<String, String> getClassMapping() {
		return classMapping;
	}

	public void setClassMapping(java.util.Hashtable<String, String> classMapping) {
		this.classMapping = classMapping;
	}

	private java.util.Hashtable<String,String> classMapping = new java.util.Hashtable<String,String>();
	
	public boolean hasItem(String theItemClass){
		return classMapping.containsKey(theItemClass);
	}

	public Class getClassForItem(String theItemClass){
		if( !this.hasItem(theItemClass)){
			return null;
		};
		
		String tmpClassName = classMapping.get(theItemClass);
		if (tmpClassName != null){
			try {
				return Class.forName(tmpClassName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Class addClassForItem(String theItemClass, java.lang.Object theItem){
		Class tmpClass = theItem.getClass();	
		classMapping.put(theItemClass, tmpClass.getName());
		return tmpClass;
	}

}
