package com.vispire.applications.volajj.RequestsFragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vispire.applications.volajj.MapFragment;
import com.vispire.applications.volajj.MapsActivity2;
import com.vispire.applications.volajj.R;
import com.vispire.applications.volajj.RequestDetailsActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Abo3li on 8/1/2018.
 */

public class RequestsAdapter extends ArrayAdapter<Request> {

    FirebaseStorage storage;
    StorageReference storageRef;

    private Context context;
    private FragmentManager fm;

    public RequestsAdapter(Context context, ArrayList<Request> requests, FragmentManager fm) {
        super(context, 0, requests);
        this.context = context;
        this.fm = fm;

        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.new_request_list_item,
                    parent, false);
        }

        final Request currentRequest = getItem(position);

        TextView titleTxtView = convertView.findViewById(R.id.new_req_list_item_title);
        TextView subTitleTxtView = convertView.findViewById(R.id.new_req_list_item_subtitle);
        final ImageView imageView = convertView.findViewById(R.id.new_req_list_item_image);

        titleTxtView.setText(currentRequest.title);
        subTitleTxtView.setText(currentRequest.subTitle);

        String ref = "requests/" + currentRequest.uri;

        storageRef.child(ref).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                new DownloadImageTask(imageView)
                        .execute(uri.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imageView.setImageURI(null);
            }
        });

        Button details = convertView.findViewById(R.id.new_req_list_item_action2);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RequestDetailsActivity.class);
                intent.putExtra("EXTRA_LATITUDE", currentRequest.latitude);
                intent.putExtra("EXTRA_LONGITUDE", currentRequest.longitude);
                intent.putExtra("EXTRA_TYPE", currentRequest.title);
                intent.putExtra("EXTRA_STATUS", currentRequest.subTitle);
                intent.putExtra("EXTRA_USER", currentRequest.user);
                context.startActivity(intent);
            }
        });

        Button accept = convertView.findViewById(R.id.new_req_list_item_action1);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapsActivity2.class);
                intent.putExtra("EXTRA_LATITUDE", currentRequest.latitude);
                intent.putExtra("EXTRA_LONGITUDE", currentRequest.longitude);
                context.startActivity(intent);
                Toast.makeText(getContext(),
                        "You have accepted the request successfully", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
