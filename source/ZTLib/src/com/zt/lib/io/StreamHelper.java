package com.zt.lib.io;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;


public class StreamHelper {
	
	/**
	 * 将InputStream转换为字符串
	 * @param stream
	 * @return 转换后的字符串
	 */
	public static String toString(InputStream stream)
	{
		StringBuffer sb = new StringBuffer();
		byte[] buffer = new byte[1024];
		int length = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while (-1 != (length = stream.read(buffer))) {
				baos.write(buffer, 0, length);
			}
			sb.append(baos.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 将输入流用指定编码转为字符串
	 * @param in 输入流
	 * @param charset 指定编码格式
	 * @return 转换后的字符串
	 */
	public static String toString(InputStream in, Charset charset)
	{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(in,
				charset));
		try {
			String str = null;
			while (null != (str = br.readLine())) {
				sb.append(str).append(System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
		
	}
	
	/**
	 * 将输入流写入输出流中
	 * @param is 输入流
	 * @param os 输出流
	 */
	public static void output(InputStream is, OutputStream os)
	{
		byte[] buffer = new byte[1024];
		int count = 0;
		try {
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.flush();
				os.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
