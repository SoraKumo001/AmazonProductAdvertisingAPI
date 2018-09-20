package jp.ac.chiba_fjb.sambple.amazonapi.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class AmazonAPI {
	private final String mAccessKey;
	private final String mSecretKey;
	private final String mAssociateTag;
	private static final String ENDPOINT = "webservices.amazon.co.jp";
	private static final String URI = "/onca/xml";

	public AmazonAPI(String associateTag, String accessKey, String secretKey){
		mAssociateTag = associateTag;
		mAccessKey = accessKey;
		mSecretKey = secretKey;
	}
	public static String createUriParam(Map<String,Object> params){
		StringBuilder sb = new StringBuilder();
		try {
			boolean flag = false;
			if(params != null && params.size()>0) {
				for(String index : params.keySet()) {
					if(flag)
						sb.append("&");
					flag = true;
					String value = params.get(index).toString();
					sb.append(URLEncoder.encode(index, "UTF-8")+"="+
							URLEncoder.encode(value, "UTF-8"));
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	public static String getHttpData(String address) {
		try {
			URL url = new URL(address);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();

			int status = con.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_BAD_REQUEST) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

				StringBuilder output = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					output.append(line);
				}
				return output.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private String getSignature(String src){
		try {
			SecretKeySpec sk = new SecretKeySpec(mSecretKey.getBytes("UTF-8"), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(sk);
			return Base64.getEncoder().encodeToString(mac.doFinal(src.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public String query(Map<String, Object> params) {
		try {
			//パラメータをコピー
			Map<String, Object> fixParams = new TreeMap<String, Object>();
			for(String index : params.keySet()){
				fixParams.put(index,params.get(index));
			}
			//日付を生成
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			String now = sdf.format(new Date());
			//追加パラメータ
			fixParams.put("Service", "AWSECommerceService");
			fixParams.put("AWSAccessKeyId", mAccessKey);
			fixParams.put("AssociateTag", mAssociateTag);
			fixParams.put("Timestamp", now);

			//認証用シグネチャを作成
			String p = createUriParam(fixParams);
			String signSrc = String.format("GET\n%s\n%s\n%s",ENDPOINT,URI,p);
			String signature = getSignature(signSrc);
			String url = String.format("https://%s%s?%s&Signature=%s",
					ENDPOINT,URI,p,URLEncoder.encode(signature, "UTF-8"));
			//生成したアドレスを出力
			System.out.println(url);
			return getHttpData(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	public String getProduct(String keyword,int page) {
		Map<String, Object> params = new HashMap<>();
		params.put("Operation", "ItemSearch");
		params.put("SearchIndex", "All");
		params.put("Keywords", keyword);
		params.put("ItemPage", page);

		params.put("ResponseGroup", "Images,ItemAttributes,Offers");
		return query(params);
	}
}
