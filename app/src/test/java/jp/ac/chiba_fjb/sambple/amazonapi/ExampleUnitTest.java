package jp.ac.chiba_fjb.sambple.amazonapi;


import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import jp.ac.chiba_fjb.sambple.amazonapi.data.AmazonAPI;
import jp.ac.chiba_fjb.sambple.amazonapi.data.ItemSearchResponse;


//デバッグ用プログラム
public class ExampleUnitTest {
    static final String ASSOCIATE_TAG = "";
    static final String ACCESS_KEY = "";
    static final String SECRET_KEY = "";

    @Test
    public void main() {
        AmazonAPI amazon = new AmazonAPI(ASSOCIATE_TAG, ACCESS_KEY, SECRET_KEY);

        for (int i = 1; i <= 10; i++) {
            try {
                String p = amazon.getProduct("hdd", i);
                if (p == null) {
                    System.out.println("データの取得に失敗");
                } else {
                    Serializer serial = new Persister();
                    ItemSearchResponse res = serial.read(ItemSearchResponse.class, p);
                    System.out.format("商品総数 %d\n", res.TotalResults);
                    System.out.format("ページ数 %d\n", res.TotalPages);
                    for (ItemSearchResponse.Item item : res.items) {
                        System.out.format("%s\n新品価格 %d\n中古価格 %d\n\n", item.Title, item.NewPrice, item.UsedPrice);
                    }
                    //ページ終端なら終了
                    if (res.TotalPages == i)
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}