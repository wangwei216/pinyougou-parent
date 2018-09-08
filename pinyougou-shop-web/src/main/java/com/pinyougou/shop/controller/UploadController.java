package com.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

/**
 * @author user
 * 这个是上传图片的功能
 */
@RestController
public class UploadController {

	//这里需要拼接完整的路径，需要把配置文件中的ip地址给注入进来
	@Value("${FILE_SERVER_URL}")
	private String file_server_url;
	
	@RequestMapping("/upload")
	public Result upload(MultipartFile file){
		//因为上传文件需要获取文件名he扩展名
		String originalFilename = file.getOriginalFilename();
		System.out.println(originalFilename);
		
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);	
		try {
			//这里是通过一个工具类直接得到一个fastdfs客户端
			util.FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
			//返回的是一个文件名
			String fileId = client.uploadFile(file.getBytes(), extName);
			//拼接成完整的图片路径地址
			String url =  file_server_url+fileId;
			System.out.println("上传成功！" + url);
			return new Result(true, url);

		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传失败！");
		}
		
	}
}
