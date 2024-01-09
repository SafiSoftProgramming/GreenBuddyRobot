package safisoft.greenbuddyrobot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class SnackBarInfoControl {

    public void SnackBarInfoControlView(Context context, View v, Activity activity,String Text_info){

        // create an instance of the snackbar
        final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);

        // inflate the custom_snackbar_view created previously
        View customSnackView = activity.getLayoutInflater().inflate(R.layout.snack_bar_info, null);

        // set the background of the default snackbar as transparent
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        // now change the layout of the snackbar
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

        // set padding of the all corners as 0
        snackbarLayout.setPadding(0, 0, 0, 0);

        TextView txtv_snack_bar_info = customSnackView.findViewById(R.id.txtv_snack_bar_info);
        txtv_snack_bar_info.setText(Text_info);

        // register the button from the custom_snackbar_view layout file
       // Button bGotoWebsite = customSnackView.findViewById(R.id.gotoWebsiteButton);

        // now handle the same button with onClickListener
   //    bGotoWebsite.setOnClickListener(new View.OnClickListener() {
   //        @Override
   //        public void onClick(View v) {
   //            Toast.makeText(context, "Redirecting to Website", Toast.LENGTH_SHORT).show();
   //            snackbar.dismiss();
   //        }
   //    });

        // add the custom snack bar layout to snackbar layout
        snackbarLayout.addView(customSnackView, 0);

        snackbar.show();

    }



// show snackbar at the start of Activity
//    SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
//    snackBarInfoControl.SnackBarInfoControlView(getApplicationContext(),findViewById(android.R.id.content).getRootView(),StartNewProjectActivity.this,"Start New Project");



}
