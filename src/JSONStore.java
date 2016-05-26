 /*  JSONStore - Simple object storage for mock ups 
 *   
 * Uses the jackson ObjectMapper to create a simple JSON store for quick Model storage
 * 
 * Works with a controller to allow for Create / Edit / Delete / List functionality
 * 
 * Author: Joseph Francis, 2013.
 * 
 * */
package corelan.store;

import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;

public class JSONStore {
	ObjectMapper mapper = new ObjectMapper();
	
	
	private static final String MAPPING_FILENAME = "JSONStoreMapping.json";
	public JSONStoreMapping getMapping() {
		return mapping;
	}


	public void setMapping(JSONStoreMapping mapping) {
		this.mapping = mapping;
	}

	JSONStoreMapping mapping = new JSONStoreMapping();
	
	public String getRootDir() {
		return rootDir;
	}


	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
		
		File tmpPath = new File(this.rootDir);
		if(!tmpPath.exists()) { 
			if (!tmpPath.mkdir()){
				System.out.println("Error - could not create dir " + tmpPath);
				return;
			}
		}	
		
		String tmpMappingFN = this.rootDir + MAPPING_FILENAME;
		File tmpF = new File(tmpMappingFN);

		if(tmpF.exists()) { 
			try {
				
				this.mapping = mapper.readValue(new File(tmpMappingFN), this.mapping.getClass());	
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			mapping = new JSONStoreMapping();
		};
	}

	private String rootDir = "";
	
	public JSONStore(String theRootDir){
		this.setRootDir(theRootDir);
	}
	
	public boolean deleteItem(String theKey, String theItemClass){
		String tmpFileName = "";
		tmpFileName = this.rootDir + theItemClass + "/" + theKey + ".json";
		File tmpF = new File(tmpFileName);
		if(tmpF.exists()) { 
			return tmpF.delete();
		}
		return false;	
	}
	
	public Object loadItem(String theKey, String theItemClass){
		Object tmpRet = null;
		Class tmpClass = null;
		tmpClass = mapping.getClassForItem(theItemClass);
		
		String tmpFileName = "";
		tmpFileName = this.rootDir + theItemClass + "\\" + theKey + ".json";
		
		try {
			if( tmpClass != null){
				File tmpFile = new File(tmpFileName);
				if (tmpFile.exists()){
					tmpRet = mapper.readValue(tmpFile, tmpClass);					
				}
			}
		} catch (Exception e) {
			// ok - just no file? If error on read, send up / trace
			
		}
		return tmpRet;
	}

	private boolean saveMapping(){
		String tmpFilename = this.rootDir + MAPPING_FILENAME;
		try {
			mapper.writeValue(new File(tmpFilename), mapping);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void saveItem(String theKey, Object theItem, String theItemClass){
		try {
			Class tmpClass = theItem.getClass();
			if( !mapping.hasItem(theItemClass)){
				//--- add mapping 
				mapping.addClassForItem(theItemClass, theItem);
				this.saveMapping();
			}
			String tmpPath = this.rootDir + theItemClass;
			File tmpF = new File(tmpPath);
			if(!tmpF.exists()) { 
				if (!tmpF.mkdir()){
					System.out.println("Error - could not create dir " + tmpPath);
				}
			}
			
			String tmpFilename = tmpPath + "/" + theKey + ".json";
			mapper.writeValue(new File(tmpFilename), theItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public java.util.ArrayList<Object> getItems(String theItemClass) {
		return this.getItems(theItemClass, 100000,0);
	}

	public int getItemCount(String theItemClass){
		int tmpRet = 100;
		String tmpPath = this.rootDir + theItemClass;
		File tmpF = new File(tmpPath);
		if(!tmpF.exists() || !tmpF.isDirectory()) { 
			return tmpRet;
		}
		File[] tmpFiles = tmpF.listFiles();
		return tmpFiles.length;
	}
	
	public java.util.ArrayList<Object> getItems(String theItemClass, int theCount, int theStart) {
		java.util.ArrayList<Object> tmpRet = new java.util.ArrayList<Object>();

//System.out.println("Debug JSONSTORE-getItems theItemClass " + theItemClass );		
		//--- loop dir
		String tmpPath = this.rootDir + theItemClass;
		File tmpF = new File(tmpPath);
		if(!tmpF.exists() || !tmpF.isDirectory()) { 
			return null;
		}
		
//System.out.println("Debug JSONSTORE-getItems tmpPath " + tmpPath );		
		
		File[] tmpFiles = tmpF.listFiles();
		int tmpSize = tmpFiles.length;

//System.out.println("Debug JSONSTORE-getItems files found " + tmpSize );		
		
		int tmpStart = theStart;
		int tmpCount = theCount;
		//--- if not asking for specific count, return all
		if( tmpCount == 0 ){tmpCount = 1000000;};
		
		int tmpEndCount = (tmpStart + tmpCount);
		if( tmpEndCount > tmpSize ){
			tmpEndCount = tmpSize;
		};
		
		if( tmpCount > 0 ){
			//--- asking for specific count - check start and adjust
			if( tmpStart > 0 ){
				//--- if not valid, end now with blank list
				if (tmpStart>tmpSize){return tmpRet;};
				//--- if valid start, adjust end if needed
				if( tmpEndCount > tmpSize ){
					tmpEndCount = tmpSize;
				}
			}
		}
		for (int i = tmpStart ; i < tmpEndCount ; i++) {
			File aFile = tmpFiles[i];
			String tmpKey = aFile.getName();

			tmpKey = tmpKey.replace(".json", "");
			tmpRet.add(this.loadItem(tmpKey, theItemClass));
		}

		return tmpRet;
	}


}
