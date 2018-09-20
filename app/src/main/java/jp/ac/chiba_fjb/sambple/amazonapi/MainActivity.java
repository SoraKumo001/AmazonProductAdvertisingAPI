package jp.ac.chiba_fjb.sambple.amazonapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.util.List;

import jp.ac.chiba_fjb.sambple.amazonapi.data.AmazonAPI;
import jp.ac.chiba_fjb.sambple.amazonapi.data.ItemSearchResponse;

public class MainActivity extends AppCompatActivity {
	static final String ASSOCIATE_TAG = "";
	static final String ACCESS_KEY = "";
	static final String SECRET_KEY = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getAmazon();
	}
	void output(List<ItemSearchResponse.Item> items){
		//スレッドを作成し、データの読み出し
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView textView = findViewById(R.id.output);
				if(items != null){
					for (ItemSearchResponse.Item item : items) {
						textView.append(String.format("%s\n新品価格 %d\n中古価格 %d\n\n", item.Title, item.NewPrice, item.UsedPrice));
					}
				}else {
					textView.setText("読み込み失敗");
				}

			}
		});
	}
	void getAmazon(){
		TextView textView = findViewById(R.id.output);
		textView.setText("");
		new Thread(new Runnable() {
			@Override
			public void run() {
				AmazonAPI amazon = new AmazonAPI(ASSOCIATE_TAG, ACCESS_KEY, SECRET_KEY);

				for (int i = 1; i <= 10; i++) {
					try {
						String p = amazon.getProduct("hdd", i);
						if (p == null) {
							output(null);
							break;
						} else {
							Serializer serial = new Persister();
							ItemSearchResponse res = serial.read(ItemSearchResponse.class, p);
							output(res.items);
							//ページ終端なら終了
							if (res.TotalPages == i)
								break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
