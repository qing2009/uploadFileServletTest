package com.lx.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.RandomAccess;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class UploadFileServlet
 */
@WebServlet("/UploadFileServlet")
@MultipartConfig
public class UploadFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	long uploadedLength = 0;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadFileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doGet(request, response);
		
		//设置响应数据格式和编码
		response.setContentType("text/html;charset=utf-8");
		
		
//		Enumeration<String> headerNameEnum = request.getHeaderNames();
//		while (headerNameEnum.hasMoreElements()) {
//			String key = headerNameEnum.nextElement();
//			String vlaue = request.getHeader(key);
//			System.out.println(key+":"+vlaue);
//		}
		
		
		Part part = request.getPart("file");
//		Collection<String> headerNames = part.getHeaderNames();
//		Iterator<String> it = headerNames.iterator();
//		while (it.hasNext()) {
//			String key = it.next();
//			String value = part.getHeader(key);
//			System.out.println(key+":"+value);
//			
//		}
		
		String fileName = "temp";
		String contentDisposition = part.getHeader("content-disposition");
		if(contentDisposition!=null) {
			int startIndex = contentDisposition.indexOf("filename=\"")+"filename=\"".length();
			
			if(startIndex>=0) {
				fileName = contentDisposition.substring(startIndex,contentDisposition.length()-1);
			}
		}
		
		String filePath = this.getServletContext().getRealPath("/upload");
		
		System.out.println("filePath:"+filePath);
		System.out.println("fileName:"+fileName);
		
		long fileSize = Long.valueOf(request.getHeader("content-length"));//上传文件大小
		
		File uploadFile = new File(filePath,fileName);
		if (uploadFile.exists()) {
			if (uploadFile.length()<fileSize) {
				uploadedLength = uploadFile.length();
				System.out.println("已上传大小："+uploadedLength);
			}else {
				response.getWriter().write("文件存在！");
				return;
				
			}
		}
		
		InputStream iStream = part.getInputStream();
		
		
//		FileOutputStream fStream = new FileOutputStream(new File(filePath,fileName));
//		int len = 0;
//		byte[] b = new byte[1024*1024];
//		while((len=iStream.read(b)) !=-1) {
//			fStream.write(b,0,len);
//			uploadedLength = uploadedLength+len;
//		}
//		fStream.close();
		
		RandomAccessFile randomAccess = new RandomAccessFile(uploadFile, "rws");
		if (uploadedLength>0) {
			long inputStreamSkip = iStream.skip(uploadedLength);
			System.out.println("上传inputStreamSkip："+inputStreamSkip);
			randomAccess.seek(uploadedLength);
		}
		
		
		boolean uploadFlag = true;
		int len = 0;
		byte[] b = new byte[1024*1024];
		while(uploadFlag && (len=iStream.read(b)) !=-1) {
			randomAccess.write(b,0,len);
			uploadedLength = uploadedLength+len;
			System.out.println("上传进度："+uploadedLength);
//			response.getWriter().write("上传进度："+uploadedLength+"<br>");
			
//			if (uploadedLength>98148520) {
//				randomAccess.close();
//				iStream.close();
//				part.delete();
//				return;
//			}
		}
		uploadedLength = 0;
		
		randomAccess.close();
		iStream.close();
		part.delete();
	}
	
	/**
	 * 断点续传分析：
	 * 1、记录上传文件的已上传进度。
	 * 	每次写入成功，都记录已上传的中字节数。
	 * 2、根据进度读取文件并完成上传。
	 */

}
