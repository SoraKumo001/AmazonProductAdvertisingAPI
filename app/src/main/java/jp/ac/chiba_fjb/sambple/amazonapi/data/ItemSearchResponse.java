package jp.ac.chiba_fjb.sambple.amazonapi.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

//データ構造
@Root(name = "ItemSearchResponse", strict = false)
public class ItemSearchResponse {
    @Root(name = "Item", strict = false)
    public static class Item {
        @Path("ItemAttributes")
        @Element(required = false)
        public String Title;

        @Path("OfferSummary/LowestNewPrice")
        @Element(required = false, name = "Amount")
        public int NewPrice;

        @Path("OfferSummary/LowestUsedPrice")
        @Element(required = false, name = "Amount")
        public int UsedPrice;
    }


    @Path("Items")
    @Element
    public int TotalResults;
    @Path("Items")
    @Element
    public int TotalPages;
    @Path("Items")
    @ElementList(inline = true, required = false, name = "item")
    public List<Item> items = new ArrayList<>();
}
