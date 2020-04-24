package com.example.locationml;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class Sender extends AsyncTask<Void,Void,String>{

    Context c;
    String urlAddress;
    String place,likelihood;

    ProgressDialog pd;

    public Sender(Context c, String urlAddress,String place,String likelihood) {
        this.c = c;
        this.urlAddress = urlAddress;
        this.place=place;
        this.likelihood=likelihood;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd=new ProgressDialog(c);
        pd.setTitle("Send");
        pd.setMessage("Sending..Please wait");
        pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.send();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        pd.dismiss();

        if(response != null)
        {
            //SUCCESS
            Toast.makeText(c,response,Toast.LENGTH_LONG).show();
        }else
        {
            //NO SUCCESS
            Toast.makeText(c,"Unsuccessful "+response,Toast.LENGTH_LONG).show();
        }
    }

    private String send()
    {

        HttpURLConnection con=Connector.connect(urlAddress);

        if(con==null)
        {
            return null;
        }
        try
        {
            OutputStream os=con.getOutputStream();

            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            bw.write(new DataPackager(place,likelihood).packData());

            bw.flush();

            bw.close();
            os.close();

            int responseCode=con.getResponseCode();

            if(responseCode==con.HTTP_OK)
            {
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer response=new StringBuffer();

                String line;

                while ((line=br.readLine()) != null)
                {
                    response.append(line);
                }
                br.close();
                return response.toString();
            }else
            {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
