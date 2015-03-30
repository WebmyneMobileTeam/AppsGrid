package com.project.appsgrid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private GridView gridApps;
    private ArrayList<PInfo> apps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridApps = (GridView) findViewById(R.id.grid_apps);
        new GetApplications().execute();
    }


    private class GetApplications extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(MainActivity.this, "Please Wait", "Fetching Applications");
        }

        @Override
        protected Void doInBackground(Void... params) {

            apps = getPackages2();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            fillGrid();
        }
    }

    private void fillGrid() {
        MyCustomAdapter adapter = new MyCustomAdapter(MainActivity.this, apps);
        gridApps.setAdapter(adapter);

    }

    public class MyCustomAdapter extends BaseAdapter {

        private final Activity context;
        private final ArrayList<PInfo> programs;

        public class ViewHolder {

            public TextView text;
            public ImageView image;


        }

        public MyCustomAdapter(Activity context, ArrayList<PInfo> programs) {
            this.context = context;
            this.programs = programs;
        }

        @Override
        public int getCount() {
            return programs.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            if (rowView == null) {

                LayoutInflater inflater = context.getLayoutInflater();

                int layout = 0;
                layout = R.layout.item_grid;
                rowView = inflater.inflate(layout, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.txtAppName);
                viewHolder.image = (ImageView) rowView.findViewById(R.id.imgAppIcon);


                rowView.setTag(viewHolder);
            }
            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            PInfo info = programs.get(position);
            holder.image.setImageDrawable(info.icon);
            holder.text.setText(info.appname);

            return rowView;
        }
    }


    class PInfo {
        private String appname = "";
        private String pname = "";
        private String versionName = "";
        private int versionCode = 0;
        private Drawable icon;

        private void prettyPrint() {
            Log.e(appname + "\t" + pname + "\t" + versionName + "\t" + versionCode, "");
        }
    }


    private ArrayList<PInfo> getPackages2() {

        ArrayList<PInfo> apps = new ArrayList<>();

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                String currAppName = pm.getApplicationLabel(packageInfo).toString();
                //This app is a non-system app
                PInfo newInfo = new PInfo();
                newInfo.appname = currAppName;
                newInfo.pname = packageInfo.packageName;
                newInfo.icon = getPackageManager().getApplicationIcon(packageInfo);
                apps.add(newInfo);


            }
        }

        return apps;
    }





    private ArrayList<PInfo> getPackages() {
        ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
        final int max = apps.size();
        for (int i = 0; i < max; i++) {
            apps.get(i).prettyPrint();
        }
        return apps;
    }

    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);

            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }

            if (isSystemPackage(packs.get(i))) {
                continue;
            }

            PInfo newInfo = new PInfo();
            newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
            res.add(newInfo);
        }


        return res;
    }

    private boolean isSystemPackage(PackageInfo packageInfo) {
        //   return ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        return ((packageInfo.applicationInfo.flags) != 0);
    }
}
