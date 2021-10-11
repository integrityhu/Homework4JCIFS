import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import jcifs.CIFSException;
import jcifs.Configuration;
import jcifs.SmbResource;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFileInputStream;

public class Homework4JCIFS {

	public static void main(String[] args) {
		String fileUrl = "smb://orangepilite2/backupSSD/downloads/wincmd.key";
		String dirUrl = "smb://WORKGROUP/";
		

		try {
			getDirFromSMBShare(args[0],args[1],dirUrl);
			getFileFromSMBShareContext(args[0],args[1],fileUrl,"/home/pzoli/wincmd.key");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void getDirFromSMBShare(String user, String pass, String path) throws MalformedURLException, SmbException {
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("WORKGROUP", user, pass);
		SmbFile smbFile = new SmbFile(path, auth);
		if (smbFile.isDirectory()) {
			String[] content = smbFile.list();
			for(String file: content) {
				System.out.println(file);
			}
		}
		
	}

	public static void getFileFromSMBShareContext(String user,String pass,String fileUrl ,String destination) throws IOException {
		Properties cifsProps = new Properties();
		cifsProps.setProperty("jcifs.smb.client.domain", "WORKGROUP");
		cifsProps.setProperty("jcifs.smb.client.username", user);
		cifsProps.setProperty("jcifs.smb.client.password", pass);
//		cifsProps.setProperty("jcifs.smb.client.enableSMB2","true");
//		cifsProps.setProperty("jcifs.smb.client.disableSMB1","false");
//		cifsProps.setProperty("jcifs.smb.client.ipcSigningEnforced","false");
//		cifsProps.setProperty("jcifs.smb.client.maxVersion","SMB311");
//		cifsProps.setProperty("jcifs.smb.client.minVersion","SMB202");
//		cifsProps.setProperty("jcifs.smb.client.port139.enabled","true");
//		cifsProps.setProperty("jcifs.smb.client.enableSMB2","true");
//		cifsProps.setProperty("jcifs.smb.client.useSMB2Negotiation", "true");
		
		Configuration config = new PropertyConfiguration(cifsProps);
		BaseContext context = new BaseContext(config);
		SmbResource resource = context.get(fileUrl);

		if (!(resource instanceof jcifs.smb.SmbFile)) {
		    throw new CIFSException("File URL does not point to a file on a network share");
		}

		try (InputStream in = ((jcifs.smb.SmbFile) resource).getInputStream(); OutputStream file = new FileOutputStream(destination)) {
			IOUtils.copy(in, file);
		 } finally {
		    context.close();
		}

	}
	public static void getFileFromSMBShare(String user,String pass,String path,String destination) throws IOException {
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("WORKGROUP", user, pass);
		SmbFile smbFile = new SmbFile(path, auth);
		try (SmbFileInputStream in = new SmbFileInputStream(smbFile); OutputStream file = new FileOutputStream(destination)) {
			IOUtils.copy(in, file);
		} finally {

		}
		System.out.println("completed ...nice !");
	}

}
