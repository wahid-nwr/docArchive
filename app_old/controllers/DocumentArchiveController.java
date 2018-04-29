package controllers;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import org.apache.commons.codec.binary.Base64;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import models.UploadFile;
import models.DownloadFile;

import play.Play;
import javax.persistence.Query;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Http.Header;
import play.server.ServletWrapper;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.util.Iterator;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import javax.validation.Valid;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.ExternalRestrictions;
import controllers.deadbolt.JSON;

import models.Role;

import play.Logger;
import play.data.FileUpload;
import play.data.validation.Validation;
import play.mvc.With;

import play.db.DB;
import java.sql.ResultSet;
import java.sql.SQLException;

@With(Deadbolt.class)
public class DocumentArchiveController extends Controller{


@ExternalRestrictions("File Archiving")
    public static void docreplacelist(){
    	List<models.Docreplaceconfig> docreplaceconfigList = models.Docreplaceconfig.findAll();
		render(docreplaceconfigList);
    }

@ExternalRestrictions("File Archiving")
    public static void createreplace(){
		render();
    }
@ExternalRestrictions("File Archiving")
    public static void editreplace(Long id){
		models.Docreplaceconfig docreplaceconfig= models.Docreplaceconfig.findById(id);
		render("@createreplace",docreplaceconfig);
    }    

@ExternalRestrictions("File Archiving")
    public static void replacedoc(Long id){
		List<models.DocumentSet> documentSetList = models.DocumentSet.findAll();
		models.Docreplaceconfig docreplaceconfig= models.Docreplaceconfig.findById(id);
		//render(uploadFileList);
		render(documentSetList,docreplaceconfig);
    }    
@ExternalRestrictions("File Archiving")
    public static void submitreplacetext(){
		String docreplaceconfigId = request.params.get("docreplaceconfig.id");
		String documentSetId = request.params.get("documentSet.id");
		String[] replaceTexts = params.getAll("replaceText");
		String[] toBeReplaceWithTexts = params.getAll("toBeReplaceWith");
		
		models.Docreplaceconfig docreplaceconfig = null;
		models.DocumentSet documentSet = null;
		models.DownloadFile downloadFile = null;
		String parameters = "trans=" + URLEncoder.encode("udjc-example.ktr");
		String files = "", replace = "", stringValue = "", description = "", file = "";
		int iteration = 0;
		
		if(docreplaceconfigId!=null && !docreplaceconfigId.equals("null") && docreplaceconfigId.length()>0)
		{
			docreplaceconfig = models.Docreplaceconfig.findById(Long.parseLong(docreplaceconfigId));
		}
		
		if(documentSetId!=null && !documentSetId.equals("null") && documentSetId.length()>0)
		{
			documentSet = models.DocumentSet.findById(Long.parseLong(documentSetId));
		}
		
		ArrayList<String> replaceList = new ArrayList<String>();
		for(int i = 0; replaceTexts!=null && i<replaceTexts.length;i++)
		{
			replaceList.add(replaceTexts[i]);
		}
		if(replaceList!=null && replaceList.size()>0)
		{
			downloadFile = new DownloadFile();
			List<UploadFile> documents = null;
			long timestamp = 0L;
			String newFiles = "", downloadFileNames = "", fileLocation = "";
			int index = 0;
			if(documentSet!=null)
			{
				documents = documentSet.documents;
				for(UploadFile uploadFile:documents)
				{
					System.out.println("System.currentTimeMillis()::"+System.currentTimeMillis());
					timestamp = System.currentTimeMillis();
					fileLocation = uploadFile.filelocation;
					if(files.length()>0)
					{
						files += "%paramseparate%"+uploadFile.filelocation;
						newFiles += "%paramseparate%"+timestamp+"-"+index;
						downloadFileNames += ";"+fileLocation.substring(0, fileLocation.lastIndexOf("."))+"-"+timestamp+"-"+index+
								fileLocation.substring(fileLocation.lastIndexOf("."), fileLocation.length());
					}
					else
					{
						files += ""+uploadFile.filelocation;
						newFiles += timestamp+"-"+index;
						downloadFileNames += fileLocation.substring(0, fileLocation.lastIndexOf("."))+"-"+timestamp+"-"+index+
								fileLocation.substring(fileLocation.lastIndexOf("."), fileLocation.length());
					}
					file = uploadFile.filelocation;
					file = file.substring(file.lastIndexOf("/")+1, file.length());
					System.out.println("file:::"+file);
					if(description.length()>0)
					{
						description += "\nReplaced content of "+file+ " file;";
					}
					else
					{
						description = "Replaced content of "+file+ " file;";
					}
					index++;
				}
			}
			parameters += "&files="+URLEncoder.encode(files);
			System.out.println("description::"+description);
			System.out.println("docset::"+documentSet.name);
			
			Set<String> keyset = null;
			String value = "";
			String toReplace = "";
			for(int i = 0;i<replaceList.size();i++)
			{
				toReplace = replaceList.get(i);
				value = toBeReplaceWithTexts[i];
				if(stringValue.length()>0)
				{
					stringValue += "%paramseparate%"+toReplace;
				}
				else
				{
					stringValue += ""+toReplace;
				}
				if(replace.length()>0)
				{
					replace += "%paramseparate%"+value;
				}
				else
				{
					replace += ""+value;
				}
			}
			parameters += "&replace="+URLEncoder.encode(replace);
			parameters += "&stringvalue="+URLEncoder.encode(stringValue);
			parameters += "&iteration="+URLEncoder.encode(""+replaceList.size());
			parameters += "&newFiles="+URLEncoder.encode(newFiles);
			System.out.println("parameters:::"+parameters);
			System.out.println("download:::"+downloadFile.toString());
			downloadFile.description = description;
			downloadFile.locations = downloadFileNames;
			//startPentahoJob("runTrans/?trans=udjc-example&level=Debug", "test.docx");
			//downloadFile.setDesc(description);
			//downloadFile.setDesc("test");
			downloadFile.save();
			startPentahoTransformation("udjc-example", parameters);
			}
			//docreplaceconfig.replaceList = replaceList;
			//docreplaceconfig.save();
		List<models.Docreplaceconfig> docreplaceconfigList = models.Docreplaceconfig.findAll();
		
		render("@docreplacelist",docreplaceconfigList);
	}
	
	public static String getPentahoStartTransUrl()
	{	
		String ip = "localhost";
		int port = 8081;
		String url = "http://"+ip+":"+port+"/kettle/executeTrans/";
		System.out.println("url:::"+url);
		return url;
	}
	
	public static String connectToAuthenticatedUrl(String urlToRoute,String parameters)
	{
		String result = "";
		System.out.println("connecting authenticated url");
		try {
			//String webPage = "http://localhost:8081/kettle/runJob/?job=kexcetle&SQL=SELECT+*+FROM+User+where+id%3D1";
			String webPage = urlToRoute;
			/*
			if(parameters!=null && !parameters.equals("null") && parameters.length()>0)
			{
				webPage = webPage+"?"+parameters;
			}
			*/
			String name = "cluster";
			String password = "012074";

			String authString = name + ":" + password;
			System.out.println("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			System.out.println("Base64 encoded auth string: " + authStringEnc);
			
			URL url = new URL(webPage);
			
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
		    String data = "product[title]=" + URLEncoder.encode("title") +
	                "&product[content]=" + URLEncoder.encode("content") + 
	                "&product[price]=" + URLEncoder.encode("price.toString()") +
	                "&tags=" + "tags";
		    System.out.println("parameters:::"+parameters);
		    wr.write(parameters);
		    wr.flush(); 
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			result = sb.toString();

			System.out.println("*** BEGIN ***");
			System.out.println(result);
			System.out.println("*** END ***");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String startPentahoTransformation(String transName, String parameters) {
		 
 		//Document doc = getDocumentFromUrl(getPentahoStartJobUrl(jobName), "Starting task " + jobName);
 		//Document doc = XML.getDocument(connectToAuthenticatedUrl(getPentahoStartJobUrl(jobName,fileName),""));
 		
 		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
 		//String parameters = "";
 		
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Document doc = null;
		String result = connectToAuthenticatedUrl(getPentahoStartTransUrl(),parameters);
		InputSource is = new InputSource();
		/*
		try {
			is.setCharacterStream(new StringReader(result));
			System.out.println("result::"+result);
			//doc = builder.parse(result);
			doc = builder.parse(is);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String status = getFirstElementByTagName(doc, "result");
		System.out.println("status::"+status);
		String STATUS_OK = "OK";
		if (STATUS_OK.equals(status)) {
			String pentahoJobId = getFirstElementByTagName(doc, "id");
			return pentahoJobId;
		} else if (status != null) {
			System.out.println("Pentaho start job {} failed, status: {}, message: {}"+ transName + status);
			return null;
		}
	 
		System.out.println("Pentaho start job {} connection error, message: {}"+ transName );
	    */
		return null;
	}
@ExternalRestrictions("File Archiving")
    public static void docsetlist(){
    	List<models.DocumentSet> documentSetList = models.DocumentSet.findAll();
		render(documentSetList);
    }
@ExternalRestrictions("File Archiving")
    public static void downloadfilelist(){
    	List<models.DownloadFile> downloadFileList = models.DownloadFile.findAll();
		render(downloadFileList);
    }
    @ExternalRestrictions("File Archiving")
    public static void downloadfiles(Long id){
		models.DownloadFile downloadFile = models.DownloadFile.findById(id);
		//render(uploadFileList);
		render(downloadFile);
    }    
    
@ExternalRestrictions("File Archiving")
    public static void doceditor(String location){
		System.out.println("location:::"+location);
		render(location);
    } 
@ExternalRestrictions("File Archiving")
    public static void createdocset(){		
		List<models.UploadFile> uploadFileList = models.UploadFile.findAll();
		render(uploadFileList);
    }
    
@ExternalRestrictions("File Archiving")
    public static void editdocset(Long id){
		models.DocumentSet documentSet = models.DocumentSet.findById(id);
		List<models.UploadFile> uploadFileList = models.UploadFile.findAll();
		//render(uploadFileList);
		render("@createdocset",documentSet,uploadFileList);
    }    
    
@ExternalRestrictions("File Archiving")
    public static void submitdocset() throws SQLException{
		String documentSetId = request.params.get("documentSet.id");
		String documentSetName = request.params.get("documentSet.name");
		//String documentsIdSet = request.params.get("documentSet.documents");
		
		String[] documentsIdSet = params.getAll("documentSet.documents");
		System.out.println("documentsIdSet:::"+documentsIdSet);
		models.DocumentSet documentSet = new models.DocumentSet();		
		if(documentSetId!=null && !documentSetId.equals("null") && documentSetId.length()>0)
		{
			documentSet = models.DocumentSet.findById(Long.parseLong(documentSetId));
		}
		documentSet.name = documentSetName;
		ArrayList<models.UploadFile> documents = new ArrayList<models.UploadFile>();
		String queryStr = "delete from DocumentSet_UploadFile where DocumentSet_id = "+documentSetId;
		//Query query = em.createQuery(queryStr);
		//int updateCount = em.executeUpdate();
		
		ResultSet resultSet;
		java.sql.Connection conn = play.db.DB.getConnection();
		try {
			java.sql.Statement stmt = conn.createStatement();
			try {
				stmt.execute(queryStr);
				System.out.println("deleting elements::"+queryStr);
			} finally {
				stmt.close();
			}
		}
		catch(java.sql.SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally {
			conn.close();
		}
		for(int i = 0; documentsIdSet!=null && i<documentsIdSet.length;i++)
		{
			//documents.add(replaceTexts[i]);
			if(documentsIdSet[i]!=null && !documentsIdSet[i].equals("null") && documentsIdSet[i].length()>0)
			{
				models.UploadFile uploadFile= models.UploadFile.findById(Long.parseLong(documentsIdSet[i]));
				documents.add(uploadFile);
			}
		}
		if(documents!=null && documents.size()>0)
		{
			documentSet.documents.addAll(documents);
			//documentSet.documents = documents;
			documentSet.save();
		}
		List<models.DocumentSet> documentSetList = models.DocumentSet.find("order by id desc limit 1,10").fetch();
		render("@docsetlist",documentSetList);
	}

@ExternalRestrictions("File Archiving")
    public static void scandoc(){    	
		render();
    }
    
    @ExternalRestrictions("File Archiving")
    public static void scanner(){    	
		render();
    }
    @ExternalRestrictions("File Archiving")
    public static void webScanning(){    	
		render();
    }
    public static String executeCommand(String[] cmd) throws java.io.IOException,java.lang.InterruptedException{
		StringBuffer theRun = null;
		//try {
			Process process = Runtime.getRuntime().exec(cmd);

			java.io.BufferedReader reader = new java.io.BufferedReader(
					new java.io.InputStreamReader(process.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				theRun = output.append(buffer, 0, read);
				System.out.println("output building");
			}
			reader.close();
			process.waitFor();
		/*
		} 
		catch(java.io.IOException e) {
			//throw new java.lang.RuntimeException(e);
			e.printStackTrace();
		} catch (java.lang.InterruptedException e) {
			//throw new java.lang.RuntimeException(e);
			e.printStackTrace();
		}
		* */
		
		return null;
	}
@ExternalRestrictions("File Archiving")
    public static void scanindex(){    	
		/*Runtime rt = Runtime.getRuntime();
		try {
			//"/bin/sh", "-c", "cd /var; ls -l"
			System.out.println("starting");
			//rt.exec(new String[]{"/bin/bash","cd /home/wahid/Downloads/docArchive/scan/lib/"});
			//rt.exec(new String[]{"/bin/bash","/home/wahid/jre1.8.0_92/bin/java -jar /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.host.jar"});
			executeCommand(new String[]{"/bin/bash","nohup /home/wahid/jre1.8.0_92/bin/java -jar /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.host.jar &"});
			System.out.println("started");
			///home/wahid/jre1.8.0_92/bin/java -jar /usr/share/leadtools/web\ scanning\ application/lib/leadtools.sane.host.jar 
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (java.lang.InterruptedException e) {
			//throw new java.lang.RuntimeException(e);
			e.printStackTrace();
		}*/
		render();
    }
	
@ExternalRestrictions("File Archiving")
    public static void list(){
    	List<models.UploadFile> uploadFileList = models.UploadFile.findAll();
		render(uploadFileList);
    }

@ExternalRestrictions("File Archiving")
    public static void create(){
		render();
    }

@ExternalRestrictions("File Archiving")
    public static void edit(Long id){
		models.UploadFile uploadFile= models.UploadFile.findById(id);
		render("@create",uploadFile);
    }
	
	@ExternalRestrictions("File Archiving")
    public static void submit(@Valid models.UploadFile uploadFile){
		/*
		String TMP_DIR_PATH = "/tempfiles";
		File tmpDir = new File("/tempfiles");
		String DESTINATION_DIR_PATH ="/files";
		File destinationDir = new File("/files");
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
       
        //Set the size threshold, above which content will be stored on disk.
        fileItemFactory.setSizeThreshold(1*1024*1024); //1 MB
       
        //Set the temporary directory to store the uploaded files of size above threshold.
        fileItemFactory.setRepository(tmpDir);
        ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
        try {
       
            //Parse the request
            List<?> items = uploadHandler.parseRequest((HttpServletRequest) request.args.get(ServletWrapper.SERVLET_REQ));
            Iterator<?> iterator = items.iterator();
            while(iterator.hasNext()) {
                FileItem item = (FileItem) iterator.next();
               
                //Handle Form Fields
                if(item.isFormField()) {
                    //System.out.println(destinationDir.getAbsolutePath()+"File Name = "+item.getFieldName()+", Value = "+item.getString());
                	System.out.println("Field Name = "+item.getFieldName()+", Value = "+item.getString());
                	if(item.getFieldName().equals("filename"))
                	{
                		uploadFile.filename = item.getString();
                	}
                   
                } 
               
                //Handle Uploaded files.
                else {
                    System.out.println("file Field Name = "+item.getFieldName()+
                            ", File Name = "+item.getName()+
                            ", Content type = "+item.getContentType()+
                            ", File Size = "+item.getSize());
                    //Write file to the ultimate location.
                    File file = new File(destinationDir,item.getName());
                    uploadFile.filelocation = file.getAbsolutePath();
                    item.write(file);
                }              
            }
        }catch(FileUploadException ex) {
            System.out.println("Error encountered while parsing the request::"+ex);
        } catch(Exception ex) {
            System.out.println("Error encountered while uploading file:::"+ex);
        }
		*/
		uploadFile.dateOfUpload=Calendar.getInstance().getTime();
		if(!params.get("uploadFile.uploadedFile").isEmpty())
		{

		   Logger.info("In uploadFile File");
		   FileUpload uploads = (FileUpload) params.get("uploadFile.uploadedFile", FileUpload.class);
		   String uploaddir = Play.configuration.getProperty("application.upload.path");
		   if(uploads!=null)
		   {
				
			   long  n = Calendar.getInstance().getTimeInMillis();
			   String path="uploadFile"+File.separator;
			   //System.out.println("uploads.getFileName()::"+file.getName());
			   System.out.println("uploads.getFileName()::"+uploads.getFileName());
			   String uploadFileName = uploads.getFileName();
			   //uploads.asFile(path + uploadFileName);
			   File newFile = uploads.asFile(uploaddir+uploadFileName);
			   uploadFile.filename = uploads.getFileName();
			   uploadFile.filelocation = newFile.getAbsolutePath();
			   uploadFile.save();
		   }
		}
		/*if(Validation.hasErrors()) {
			Logger.info("Error!!  "+ Validation.errors().toString());
        	render("@create", uploadFile);
        }*/
		
		
		
		List<models.UploadFile> uploadFileList = models.UploadFile.findAll();
		render("@list",uploadFileList);
    }

	@ExternalRestrictions("File Archiving")
   	public static void downloadFile(Long id)
   	{
    	models.UploadFile uploadFile=models.UploadFile.findById(id);
		String path = "uploadFile/" +uploadFile.filename;
       	response.setContentTypeIfNotSet("application/force-download");
       	        //response.setContentLength(-1);
       	response.setHeader("Content-Transfer-Encoding", "binary");
       	response.setHeader("Content-Disposition","attachment; uploadFilename=\"" + uploadFile.filename );
       	renderBinary(new File(path));
   	}
	@ExternalRestrictions("Media Archiving")
    public static String delete(Long id){
		Boolean code = false;
    	String msg ="User can not be deleted";
    	Logger.info("Deleted start!!!!" +id);
    	if(request.isAjax()) {
    		
	    	notFoundIfNull(id, "id not provided");
	    	models.UploadFile uploadFile = models.UploadFile.findById(id);
	    	notFoundIfNull(uploadFile, "user not found");
	    	try{
	    		uploadFile.delete();
	    		code = true;
	    		msg = "File deleted successfully";
	    		Logger.info("Deleted!!!!");
	    	}
	    	catch(Exception e){
	    		Logger.info("Not possible" + 1000);
	    		code=false;
	    		
	    	}
    	}
    	if(code){
    		response.status=200;
    		return msg;
    		
    	}
    	else{
    		
    		response.status=303;
    		return msg;
    		
    	}
    }
	
}
	
	
