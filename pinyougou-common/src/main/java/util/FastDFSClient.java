package util;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

/**
 * @author user
 * fastDFS的工具类，方便直接调用
 */
public class FastDFSClient {

	private TrackerClient trackerClient = null;
	private TrackerServer trackerServer = null;
	private StorageServer storageServer = null;
	
	private StorageClient1 storageClient = null;
	
	public FastDFSClient(String conf) throws Exception {
		if (conf.contains("classpath:")) {
			conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
			System.out.println(conf);
		}
		 // 1、加载配置文件，配置文件中的内容就是 tracker 服务的地址。
		ClientGlobal.init(conf);
		// 2、创建一个 TrackerClient 对象。直接 new 一个。
		trackerClient = new TrackerClient();
		//3、通过trackerClient的客户端得到一个连接对象，有点像JDBC
		trackerServer = trackerClient.getConnection();
		//4、创建一个StoregeServer的引用
		storageServer = null;
		//5、创建一个StroegeClient对象，这个就需要把trackerServer和storageServer穿进去
		storageClient = new StorageClient1(trackerServer, storageServer);
	}
	
	/**
	 * 上传文件方法
	 * <p>Title: uploadFile</p>
	 * <p>Description: </p>
	 * @param fileName 文件全路径
	 * @param extName 文件扩展名，不包含（.）
	 * @param metas 文件扩展信息
	 * @return
	 * 
	 * @throws Exception
	 */
	public String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
		String result = storageClient.upload_file1(fileName, extName, metas);
		return result;
	}
	
	public String uploadFile(String fileName) throws Exception {
		return uploadFile(fileName, null, null);
	}
	
	public String uploadFile(String fileName, String extName) throws Exception {
		return uploadFile(fileName, extName, null);
	}
	
	/**
	 * 上传文件方法
	 * <p>Title: uploadFile</p>
	 * <p>Description: <
	 * /p>
	 * @param fileContent 文件的内容，字节数组
	 * @param extName 文件扩展名
	 * @param metas 文件扩展信息
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {
		
		String result = storageClient.upload_file1(fileContent, extName, metas);
		return result;
	}
	
	public String uploadFile(byte[] fileContent) throws Exception {
		return uploadFile(fileContent, null, null);
	}
	
	public String uploadFile(byte[] fileContent, String extName) throws Exception {
		return uploadFile(fileContent, extName, null);
	}
}
