package com.finalproject.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.finalproject.app.util.Utility;


public class MainActivity extends FragmentActivity {

//	private final static String LOG_TAG = MainActivity.class.getSimpleName();
	
	private String courseName = "";
	private TextView notifTextView;
	private TextView locationTextView;
	private TextView courseTextView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notifTextView = (TextView)this.findViewById(R.id.notifTextView);
        courseTextView = (TextView)this.findViewById(R.id.courseTextView);
        locationTextView = (TextView)this.findViewById(R.id.locationTextView);

    	final String PRE_TEXT = this.getResources().getString(R.string.notifText);
        
        notifTextView.setText(PRE_TEXT + "");
        courseTextView.setText("");
        locationTextView.setText("");
        
        notifTextView.setOnClickListener(clickListener);
        courseTextView.setOnClickListener(clickListener);
        locationTextView.setOnClickListener(clickListener);
   
        courseName = getIntent().getStringExtra(Utility.COURSE_NAME);
        
        if(courseName != null) {
        
        	MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.splashsound0);
    		mediaPlayer.stop();
    		
        	Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        	detailIntent.putExtra(Utility.COURSE_NAME, courseName);
			startActivity(detailIntent);
        }
		
        String[] details = Utility.nextClass(this);
        
        if(details != null) {
        	courseTextView.setText(details[0]);
        	notifTextView.setText(PRE_TEXT + details[1]);
        	locationTextView.setText(details[2]);
        	
        	courseName = details[0];
        }
        else {
        	notifTextView.setText("No classes");
        }
    }
    
    private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if(courseName != null) {
				Intent intent = new Intent(MainActivity.this, DetailActivity.class);
				intent.putExtra(Utility.COURSE_NAME, courseName);
				startActivity(intent);
			}
		}
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch(item.getItemId()) {
    		
    	case R.id.add:    	
    		
    		DialogFragment dialog = new OptionDialogFragment();
            dialog.show(getSupportFragmentManager(), "DialogFragment");

    		return true;
    		
    	case R.id.view:
    		
    		Intent intent = new Intent(MainActivity.this, AddActivity.class);
			intent.putExtra("FRAGMENT", Utility.VIEW);
			startActivity(intent);
			
    		return true;
    		
    	default:
    	        return super.onOptionsItemSelected(item);
    	}
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		final String PRE_TEXT = this.getResources().getString(R.string.notifText);
		
		String[] details = Utility.nextClass(this);
        
        if(details != null) {
        	courseTextView.setText(details[0]);
        	notifTextView.setText(PRE_TEXT + " " + details[1]);
        	locationTextView.setText(details[2]);
        	
        	courseName = details[0];
        }
        else {
        	notifTextView.setText("No classes");
        }
	}

	public static class OptionDialogFragment extends DialogFragment {
    	
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        	
        	builder.setTitle(R.string.choose_action);
        	builder.setItems(R.array.course_actions, dialogListener);
        	
        	// Create the AlertDialog object and return it
        	return builder.create();
        }
        
        private DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Intent intent = new Intent(getActivity(), AddActivity.class);
				
				switch(which) {
				
				
				case 0:
					
					intent.putExtra("FRAGMENT", Utility.ADD_COURSE);
					startActivity(intent);
					break;
				
				case 1:
					
					intent.putExtra("FRAGMENT", Utility.ADD_SCHEDULE);
					startActivity(intent);
					break;
					
				case 2:
					
					intent.putExtra("FRAGMENT", Utility.DELETE_COURSE);
					startActivity(intent);
					break;
					
				case 3:
					
					intent.putExtra("FRAGMENT", Utility.DELETE_SCHEDULE);
					startActivity(intent);
					break;
				}
			}
		};
    }
}
