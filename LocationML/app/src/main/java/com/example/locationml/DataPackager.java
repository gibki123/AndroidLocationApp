package com.example.locationml;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

public class DataPackager {

    String place;
    String likelihood;
    String typeOfLocation;
    String time;

    public DataPackager(String place, String likelihood,String typeOfLocation, String time) {
        this.place = place;
        this.likelihood = likelihood;
        this.typeOfLocation = typeOfLocation;
        this.time = time;
    }

    public String packData()
    {
        JSONObject jo=new JSONObject();
        StringBuffer packedData=new StringBuffer();
        try
        {
            jo.put("place",place);
            jo.put("likelihood",likelihood);
            jo.put("typeOfLocation",typeOfLocation);
            jo.put("appTime",typeOfLocation);
            Boolean firstValue=true;
            Iterator it=jo.keys();
            do {
                String key=it.next().toString();
                String value=jo.get(key).toString();
                if(firstValue)
                {
                    firstValue=false;
                }else
                {
                    packedData.append("&");
                }

                packedData.append(URLEncoder.encode(key,"UTF-8"));
                packedData.append("=");
                packedData.append(URLEncoder.encode(value,"UTF-8"));

            }while (it.hasNext());

            return packedData.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
