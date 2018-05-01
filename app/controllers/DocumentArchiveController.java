package controllers;


import play.mvc.Scope.Session;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import play.db.jpa.JPA;


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

import play.data.Upload;
import models.Role;

import play.Logger;
import play.data.FileUpload;
import play.data.validation.Validation;
import play.mvc.With;
import controllers.deadbolt.Unrestricted;

import play.db.DB;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Vector;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import models.ScanUpload;
@With(Deadbolt.class)
public class DocumentArchiveController extends Controller{

@ExternalRestrictions("File Archiving")
    public static void viewscan(Long id){
		models.ScanUpload scanUpload= models.ScanUpload.findById(id);
		render(scanUpload);
    }  
    @ExternalRestrictions("File Archiving")
    public static void scanlist(){
		String sqlQuery = "";
		String pageNo = request.params.get("page");
		int page = 1;
		int startIndex = 0;
		System.out.println("pageNo:::"+pageNo);
		if(pageNo!=null && !pageNo.equals("null") && pageNo.length()>0)
		{
			page = Integer.parseInt(pageNo);
			if(page<1)
			{
				page = 1;
			}
			startIndex = 	(page-1)*10;
		}
		String customerId = request.params.get("customerId");
		String accountId = request.params.get("accountId");
		String agreementId = request.params.get("agreementId");
		String docCategory = request.params.get("docCategory");
		Query query = null;
		EntityManager em = JPA.em();
		String whereClause = "";
		//whereClause = " where ";
		if(customerId!=null && !customerId.equals("null") && customerId.length()>0)
		{
			whereClause += " where scanUpload.customerId like '%"+customerId+"%'";
		}
		if(accountId!=null && !accountId.equals("null") && accountId.length()>0)
		{
			if(accountId.length()==12)
			{
				if(whereClause!=null && whereClause.length()>0)
				{
					whereClause += " and scanUpload.groupId like '%"+accountId+"%'";
				}
				else
				{
					whereClause += " where scanUpload.groupId like '%"+accountId+"%'";
				}
			}
			else
			{
				if(whereClause!=null && whereClause.length()>0)
				{
					whereClause += " and scanUpload.accountId like '%"+accountId+"%'";
				}
				else
				{
					whereClause += " where scanUpload.accountId like '%"+accountId+"%'";
				}

			}
		}
		
		if(agreementId!=null && !agreementId.equals("null") && agreementId.length()>0)
		{
			if(whereClause!=null && whereClause.length()>0)
			{
				whereClause += " and scanUpload.agreementId like '%"+agreementId+"%'";
			}
			else
			{
				whereClause += " where scanUpload.agreementId like '%"+agreementId+"%'";
			}
		}
		
		
		
		//query = JPA.em().createNativeQuery(sqlQuery);
		
		if(customerId!=null && !customerId.equals("null") && customerId.length()>0)
		{
			//query.setParameter("customerId",customerId);
		}
		
		
		sqlQuery = " Select count(*) from  ScanUpload scanUpload "+whereClause+"";    		
		
		System.out.println("sqlQuery:::"+sqlQuery);
		//query = JPA.em().createNativeQuery(sqlQuery);
		query = JPA.em().createNativeQuery(sqlQuery);
		int total = ( (Number)query.getSingleResult()).intValue();
		
		sqlQuery = "select scanUpload from ScanUpload scanUpload "+whereClause+" order by id desc";    		
		System.out.println("sqlQuery:::"+sqlQuery);
		query = JPA.em().createQuery(sqlQuery);
		if(whereClause!=null && !whereClause.equals("null") && whereClause.length()>0)
		{
			//startIndex = 0;
		}
		query.setFirstResult(startIndex);
		query.setMaxResults(10);
		System.out.println("startIndex:::"+startIndex);
		List<models.ScanUpload> scanUploadList = query.getResultList();
    	//List<models.ScanUpload> scanUploadList = models.ScanUpload.find("select scanUpload from ScanUpload scanUpload order by id desc limit 0,5").fetch();
		render(scanUploadList,page,total,customerId,accountId,agreementId);
    }

//@ExternalRestrictions("File Archiving")
@Unrestricted
    public static void savescanimage(){
	
	InputStream data = request.body;
	
	System.out.println("data:::"+data);
	
	String customerId = Session.current().get("customerId");
	String accountId = Session.current().get("accountId");
	String agreementId = Session.current().get("agreementId");
	String docdesclevel = Session.current().get("docdesclevel");
	
	String docDescTypeId = "";
	int level = 0 ;
	List<models.DocDescType> listOfDocDescType = null;
	models.DocDescType docDescType = null;
	models.ScanUpload scanUpload = null;
	scanUpload = new models.ScanUpload();
	if(docdesclevel!=null && !docdesclevel.equals("null") && docdesclevel.length()>0)
	{
		level = Integer.parseInt(docdesclevel);
	}
	if(level>0)
	{
		listOfDocDescType = new ArrayList<models.DocDescType>();
		for(int i = 1;i<=level;i++)
		{
			docDescTypeId = Session.current().get("docDescTypelevel_"+i);
			if(docDescTypeId!=null && !docDescTypeId.equals("null") && docDescTypeId.length()>0)
			{
				docDescType = models.DocDescType.findById(Long.parseLong(docDescTypeId));
				listOfDocDescType.add(docDescType);
			}
		}
	}
	if(listOfDocDescType!=null && listOfDocDescType.size()>0)
	{
		
		scanUpload.customerId = customerId;
		scanUpload.accountId = accountId;
		scanUpload.agreementId = agreementId;
		scanUpload.listOfDocDescType = listOfDocDescType;
	}
	
	long currenttimemillis = System.currentTimeMillis();
    
	FileOutputStream moveTo = null;
	String locations = "";
    try {
		
		//String filename = request.params.get("uploadfile");
		//System.out.println("filename:::"+filename);
		
		ObjectInputStream ois = new ObjectInputStream(data);
		Vector v = (Vector) ois.readUnshared();
		//ScanUpload scanUpload = null;
	    if(scanUpload!=null)
	    {
			
			if(v!=null && v.size()>0)
			{
				//scanUpload = new ScanUpload();
				String fx = "",ext = "";
				for(int i=0;i<v.size();i++)
				{
					if((v.get(i)) instanceof String)
					{
						ext = (String)v.get(i);
						fx = Play.getFile("").getAbsolutePath()+File.separator+"uploads"+File.separator+currenttimemillis+"_"+i+"test."+ext;						
						System.out.println("ffffff");
					}
					else
					{
						if(fx.length()>0)
						{
							/*
							File scanfile = (File)v.get(i);
							File f = new File(fx);
							System.out.println("gggggggggggggg::"+fx);	
							//java.io.FileWriter fw = new java.io.FileWriter(fx);
							//fw.write(scanfile);
							//fw.close();
							
							ByteArrayOutputStream ous = null;
							InputStream ios = null;
							try {
								byte[] buffer = new byte[4096];
								ous = new ByteArrayOutputStream();
								ios = new FileInputStream(scanfile);
								int read = 0;
								while ((read = ios.read(buffer)) != -1) {
									ous.write(buffer, 0, read);
								}
							}finally {
								try {
									if (ous != null)
										ous.close();
								} catch (IOException e) {
								}

								try {
									if (ios != null)
										ios.close();
								} catch (IOException e) {
								}
							}
							*/
							byte[] theExtractedByteArray = (byte[])v.get(i);
							System.out.println("byte array:::"+theExtractedByteArray.length);
						
							if(locations!=null && !locations.equals("null") && locations.length()>0)
							{
								locations += ";"+fx;
							}
							else
							{
								locations += fx;
							}
							//convert array of bytes into file
							FileOutputStream fileOutputStream = new FileOutputStream(fx); 
							fileOutputStream.write(theExtractedByteArray);
							fileOutputStream.close();
							fx = "";
							ext = "";
						}
					}
					
					System.out.println("vector:::"+v.size());
					//System.out.println("vector content:::"+(v.get(i)).toString());
					
				}
			
				if(scanUpload!=null)
				{
					scanUpload.locations = locations;
					//User currentUser = User.findByName(controllers.Secure.Security.connected());
					scanUpload.description = "Scan created by "+controllers.Secure.Security.connected();
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					System.out.println(dateFormat.format(date));
					scanUpload.name = "Scan created on "+dateFormat.format(date);
					scanUpload.save();
				}
			}
		}
	}
	catch(ClassNotFoundException ex)
	{
		ex.printStackTrace();
	}
	catch(FileNotFoundException ex)
	{
		ex.printStackTrace();
	}
	catch(IOException ex)
	{
		ex.printStackTrace();
	}
	/*
 	finally{
            
        }
	*/
	System.out.println("Saving image");
	List<models.ScanUpload> scanUploadList = models.ScanUpload.findAll();
	//render("@scanlist",scanUploadList);
	//redirect("http://107.170.4.9:9040/documentArchiveController/scanlist");
    }

	@ExternalRestrictions("File Archiving")
    public static void pdfview(String location,long scanId){
		ScanUpload scanUpload = ScanUpload.findById(scanId);
		int maxlevel = 0;
		String sqlQuery = "";
		Query query = null;
		EntityManager em = JPA.em();
		sqlQuery = " Select IFNULL(max(level),1) from DocDescType";    		
		
		System.out.println("sqlQuery:::"+sqlQuery);
		//query = JPA.em().createNativeQuery(sqlQuery);
		query = JPA.em().createNativeQuery(sqlQuery);
		
		maxlevel = ( (Number)query.getSingleResult()).intValue();
		Map<String,List<models.DocDescType>> docDescTypeListMap = new HashMap<String,List<models.DocDescType>>();
		List<models.DocDescType> docDescTypeList = null;
		for(int i=1;i<=maxlevel;i++)
		{
			docDescTypeList = models.DocDescType.find("level="+i).fetch();
			docDescTypeListMap.put(""+i,docDescTypeList);
		}
		location = location.substring(location.lastIndexOf("/")+1,location.length());
		location = "../uploads/"+location;
		render(location,docDescTypeListMap,scanUpload);
    }
    
    @ExternalRestrictions("File Archiving")
    public static void imageview(String location,long scanId){
		ScanUpload scanUpload = ScanUpload.findById(scanId);
		int maxlevel = 0;
		String sqlQuery = "";
		Query query = null;
		EntityManager em = JPA.em();
		sqlQuery = " Select IFNULL(max(level),1) from DocDescType";    		
		
		System.out.println("sqlQuery:::"+sqlQuery);
		//query = JPA.em().createNativeQuery(sqlQuery);
		query = JPA.em().createNativeQuery(sqlQuery);
		
		maxlevel = ( (Number)query.getSingleResult()).intValue();
		Map<String,List<models.DocDescType>> docDescTypeListMap = new HashMap<String,List<models.DocDescType>>();
		List<models.DocDescType> docDescTypeList = null;
		for(int i=1;i<=maxlevel;i++)
		{
			docDescTypeList = models.DocDescType.find("level="+i).fetch();
			docDescTypeListMap.put(""+i,docDescTypeList);
		}
		location = location.substring(location.lastIndexOf("/")+1,location.length());
		location = "../uploads/"+location;
		render(location,docDescTypeListMap,scanUpload);
    }

@ExternalRestrictions("File Archiving")
    public static void docreplacelist(){
    	List<models.Docreplaceconfig> docreplaceconfigList = models.Docreplaceconfig.findAll();
		render(docreplaceconfigList);
    }

@ExternalRestrictions("File Archiving")
    public static void twainjar(){
    	File file=new File("/home/wahid/docArchive/app/views/DocumentArchiveController/twain.jar");
		renderBinary(file);
    }
@ExternalRestrictions("File Archiving")
    public static void scanningjar(){
    	File file=new File("/home/wahid/docArchive/app/views/DocumentArchiveController/scanning.jar");
		renderBinary(file);
    }

@ExternalRestrictions("File Archiving")
    public static void ijjar(){
    	File file=new File("/home/wahid/docArchive/app/views/DocumentArchiveController/ij.jar");
		renderBinary(file);
    }
@ExternalRestrictions("File Archiving")
    public static void pluginjar(){
    	File file=new File("/home/wahid/docArchive/app/views/DocumentArchiveController/plugin.jar");
		renderBinary(file);
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
		List<models.UploadFile> uploadFileList = models.UploadFile.findAll();
		//render(uploadFileList);
		render(documentSetList,docreplaceconfig,uploadFileList);
    }    
    @ExternalRestrictions("File Archiving")
    public static void submitcreatereplacetext(){
		String docreplaceconfigId = request.params.get("docreplaceconfig.id");

		String[] replaceTexts = params.getAll("replaceText");
		models.Docreplaceconfig docreplaceconfig = null;
		if(docreplaceconfigId!=null && !docreplaceconfigId.equals("null") && docreplaceconfigId.length()>0)
		{
			docreplaceconfig = models.Docreplaceconfig.findById(Long.parseLong(docreplaceconfigId));
		}
		else
		{
			docreplaceconfig = new models.Docreplaceconfig();
		}
		ArrayList<String> replaceList = new ArrayList<String>();
		for(int i = 0; replaceTexts!=null && i<replaceTexts.length;i++)
		{
			replaceList.add(replaceTexts[i]);
		}
		docreplaceconfig.replaceList = replaceList;
		docreplaceconfig.save();
		List<models.Docreplaceconfig> docreplaceconfigList = models.Docreplaceconfig.findAll();
		render("@docreplacelist",docreplaceconfigList);
	}
@ExternalRestrictions("File Archiving")
    public static void submitreplacetext(){
		String docreplaceconfigId = request.params.get("docreplaceconfig.id");
		String[] documentsIdSet = params.getAll("documentSet.documents");
		System.out.println("documentsIdSet:::"+documentsIdSet);
		models.DocumentSet documentSet = new models.DocumentSet();		
		ArrayList<models.UploadFile> uploaddocuments = new ArrayList<models.UploadFile>();
		documentSet.name = "";
		
		for(int i = 0; documentsIdSet!=null && i<documentsIdSet.length;i++)
		{
			//documents.add(replaceTexts[i]);
			if(documentsIdSet[i]!=null && !documentsIdSet[i].equals("null") && documentsIdSet[i].length()>0)
			{
				models.UploadFile uploadFile= models.UploadFile.findById(Long.parseLong(documentsIdSet[i]));
				uploaddocuments.add(uploadFile);
			}
		}
		if(uploaddocuments!=null && uploaddocuments.size()>0)
		{
			documentSet.documents.addAll(uploaddocuments);
			//documentSet.documents = documents;
			//documentSet.save();
		}
		//String documentSetId = request.params.get("documentSet.id");
		String[] replaceTexts = params.getAll("replaceText");
		String[] toBeReplaceWithTexts = params.getAll("toBeReplaceWith");
		
		models.Docreplaceconfig docreplaceconfig = null;
		//models.DocumentSet documentSet = null;
		models.DownloadFile downloadFile = null;
		String parameters = "trans=" + URLEncoder.encode("udjc-example.ktr");
		String files = "", replace = "", stringValue = "", description = "", file = "";
		int iteration = 0;
		
		if(docreplaceconfigId!=null && !docreplaceconfigId.equals("null") && docreplaceconfigId.length()>0)
		{
			docreplaceconfig = models.Docreplaceconfig.findById(Long.parseLong(docreplaceconfigId));
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
			//System.out.println("docset::"+documentSet.name);
			
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
		//List<models.Docreplaceconfig> docreplaceconfigList = models.Docreplaceconfig.findAll();
		List<models.DownloadFile> downloadFileList = models.DownloadFile.findAll();
		render("@downloadfilelist",downloadFileList);
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
    public static void documenteditor(String location){
		System.out.println("location:::"+location);
		String filename = location.substring(location.lastIndexOf("/")+1,location.length());
		render(location,filename);
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
		System.out.println("documentsIdSet:::"+documentsIdSet.toString());
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
		if(documentSetId != null && !documentSetId.equals("null") && documentSetId.length() > 0) {
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
		List<models.DocumentSet> documentSetList = models.DocumentSet.find("order by id desc").fetch();
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
	render();
    }
	
	@ExternalRestrictions("File Archiving")
    public static void createscan(){    	
		int maxlevel = 0;
		String sqlQuery = "";
		Query query = null;
		EntityManager em = JPA.em();
		sqlQuery = " Select IFNULL(max(level),1) from DocDescType";    		
		
		System.out.println("sqlQuery:::"+sqlQuery);
		//query = JPA.em().createNativeQuery(sqlQuery);
		query = JPA.em().createNativeQuery(sqlQuery);
		
		maxlevel = ( (Number)query.getSingleResult()).intValue();
		System.out.println("maxlevel:::"+maxlevel);
		String options = "";
		List<models.DocDescType> docDescTypeList = models.DocDescType.find("level=1").fetch();
			for(models.DocDescType docDescType : docDescTypeList){
				options += "<option value=\""+docDescType.id+"\">"+docDescType.name+", level:"+docDescType.level+"</option>"; 
			}
		System.out.println("options:::"+options);
		render(maxlevel,docDescTypeList);
    }
    @ExternalRestrictions("File Archiving")
    public static void getParentDocDescTypeList(){
		String options = "";		
		if(request.isAjax()) {
			List<models.DocDescType> docDescTypeList = models.DocDescType.findAll();
			for(models.DocDescType docDescType : docDescTypeList){
				options += "<option value=\""+docDescType.id+"\">"+docDescType.name+", level:"+docDescType.level+"</option>"; 
			}
		}
		renderText(options);		
    }
    
    @ExternalRestrictions("File Archiving")
    public static void getDocDescByLevelList(Long id){
		String options = "";		
		if(request.isAjax()) {
			List<models.DocDescType> docDescTypeList = models.DocDescType.find("parent.id=?",id).fetch();
			for(models.DocDescType docDescType : docDescTypeList){
				options += "<option value=\""+docDescType.id+"\">"+docDescType.name+", level:"+docDescType.level+"</option>"; 
			}
		}
		renderText(options);		
    }
    
    @ExternalRestrictions("File Archiving")
    public static void getDocDescCategory(Long id){
		String options = "";		
		if(request.isAjax()) {
			models.DocDescType docDescType = models.DocDescType.findById(id);
			options = docDescType.descCategory;
		}
		renderText(options);		
    }
    
	@ExternalRestrictions("File Archiving")
    public static void docDescTypeList(){
    	List<models.DocDescType> docDescTypeList = models.DocDescType.findAll();
		render(docDescTypeList);
    }
	
	@ExternalRestrictions("File Archiving")
    public static void createDocDescType(){
		render();
    }
    @ExternalRestrictions("File Archiving")
    public static void submitScanDesc(){	
		//Session.current().put("webUser", username);
		String customerId = request.params.get("customerId");
		String accountId = request.params.get("accountId");
		String agreementId = request.params.get("agreementId");
		String docdesclevel = request.params.get("docdesclevel");
		
		String scanId = request.params.get("scanUpload.id");
		Session.current().put("customerId", customerId);
		Session.current().put("accountId", accountId);
		Session.current().put("agreementId", agreementId);
		Session.current().put("docdesclevel", docdesclevel);
		String docDescTypeId = "";
		int level = 0 ;
		List<models.DocDescType> listOfDocDescType = null;
		models.DocDescType docDescType = null;
		models.ScanUpload scanUpload = null;
		System.out.println("customerId:::"+customerId);
		if(scanId!=null && !scanId.equals("null") && scanId.length()>0)
		{
			scanUpload = ScanUpload.findById(Long.parseLong(scanId));
		}

		if(docdesclevel!=null && !docdesclevel.equals("null") && docdesclevel.length()>0)
		{
			level = Integer.parseInt(docdesclevel);
		}
		if(level>0)
		{
			listOfDocDescType = new ArrayList<models.DocDescType>();
			for(int i = 1;i<=level;i++)
			{
				docDescTypeId = request.params.get("docDescTypelevel_"+i);
				System.out.println("docDescTypeId:::"+docDescTypeId);
				if(docDescTypeId!=null && !docDescTypeId.equals("null") && docDescTypeId.length()>0)
				{
					docDescType = models.DocDescType.findById(Long.parseLong(docDescTypeId));
					listOfDocDescType.add(docDescType);
				}
			}
		}
		if(listOfDocDescType!=null && listOfDocDescType.size()>0)
		{
			scanUpload.listOfDocDescType.clear();
			
			if(scanUpload.listOfDocDescType!=null)
			{
				scanUpload.listOfDocDescType.clear();
			}
			else
			{
				scanUpload.listOfDocDescType = new ArrayList<models.DocDescType>(); 
			}
			scanUpload.customerId = customerId;
			System.out.println("accountId::"+accountId.length());
			if(accountId!=null && accountId.length()==12)
			{
				scanUpload.groupId = accountId;
			}
			else
			{
				scanUpload.accountId = accountId;
			}
			
			scanUpload.agreementId = agreementId;
			scanUpload.listOfDocDescType.addAll(listOfDocDescType);
			
			//scanUpload.locations = locations;
			//User currentUser = User.findByName(controllers.Secure.Security.connected());
			//scanUpload.description = "Scan created by "+controllers.Secure.Security.connected();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println(dateFormat.format(date));
			//scanUpload.name = "Scan created on "+dateFormat.format(date);
			scanUpload.save();
		}
		
		//String docDescTypelevel_1 = request.params.get("docDescTypelevel_1");
		
		String sqlQuery = "";
		String pageNo = request.params.get("page");
		int page = 1;
		int startIndex = 0;
		if(pageNo!=null && !pageNo.equals("null") && pageNo.length()>0)
		{
			page = Integer.parseInt(pageNo);
			if(page<1)
			{
				page = 1;
			}
			startIndex = 	(page-1)*10;
		}
		
		Query query = null;
		EntityManager em = JPA.em();
		String whereClause = "";
		//whereClause = " where ";
		if(customerId!=null && !customerId.equals("null") && customerId.length()>0)
		{
			whereClause += " where scanUpload.customerId like '%"+customerId+"%'";
		}
		if(accountId!=null && !accountId.equals("null") && accountId.length()>0)
		{
			if(accountId.length()==12)
			{
				if(whereClause!=null && whereClause.length()>0)
				{
					whereClause += " and scanUpload.groupId like '%"+accountId+"%'";
				}
				else
				{
					whereClause += " where scanUpload.groupId like '%"+accountId+"%'";
				}
			}
			else
			{
				if(whereClause!=null && whereClause.length()>0)
				{
					whereClause += " and scanUpload.accountId like '%"+accountId+"%'";
				}
				else
				{
					whereClause += " where scanUpload.accountId like '%"+accountId+"%'";
				}

			}
		}
		
		if(agreementId!=null && !agreementId.equals("null") && agreementId.length()>0)
		{
			if(whereClause!=null && whereClause.length()>0)
			{
				whereClause += " and scanUpload.agreementId like '%"+agreementId+"%'";
			}
			else
			{
				whereClause += " where scanUpload.agreementId like '%"+agreementId+"%'";
			}
		}
		
		
		
		//query = JPA.em().createNativeQuery(sqlQuery);
		
		if(customerId!=null && !customerId.equals("null") && customerId.length()>0)
		{
			//query.setParameter("customerId",customerId);
		}
		
		
		sqlQuery = " Select count(*) from  ScanUpload scanUpload "+whereClause+"";    		
		
		System.out.println("sqlQuery:::"+sqlQuery);
		//query = JPA.em().createNativeQuery(sqlQuery);
		query = JPA.em().createNativeQuery(sqlQuery);
		int total = ( (Number)query.getSingleResult()).intValue();
		
		sqlQuery = "select scanUpload from ScanUpload scanUpload "+whereClause+" order by id desc";    		
		System.out.println("sqlQuery:::"+sqlQuery);
		query = JPA.em().createQuery(sqlQuery);
		if(whereClause!=null && !whereClause.equals("null") && whereClause.length()>0)
		{
			//startIndex = 0;
		}
		query.setFirstResult(startIndex);
		query.setMaxResults(10);
		System.out.println("startIndex:::"+startIndex);
		List<models.ScanUpload> scanUploadList = query.getResultList();
    	//List<models.ScanUpload> scanUploadList = models.ScanUpload.find("select scanUpload from ScanUpload scanUpload order by id desc limit 0,5").fetch();
		//render(scanUploadList,page,total,customerId,accountId,agreementId);
		render("@scanlist",scanUploadList,page,total,customerId,accountId,agreementId);
	}
    @ExternalRestrictions("File Archiving")
    public static void submitDocDescType(@Valid models.DocDescType docDescType){
		if(docDescType.parent!=null)
		{
			docDescType.level = docDescType.parent.level+1;
		}
		docDescType.save();
		List<models.DocDescType> docDescTypeList = models.DocDescType.findAll();
		render("@docDescTypeList",docDescTypeList);
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
		File destinationDir = new File("/var/www/html/docfiles/files/");
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
	
	
