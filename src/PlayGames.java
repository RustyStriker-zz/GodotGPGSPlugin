package org.godotengine.godot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.Task;


public class PlayGames extends Godot.SingletonBase {

    private static PlayGames mInstance = null;

    private static final String TAG = "godotGoogle";

    protected Activity activity;
    protected Context context;
    private int instanceId = 0;

	  private GoogleSignInClient mClient;

  	private GoogleSignInAccount mAccount;
	  private AchievementsClient mAchie;
  	private LeaderboardsClient mLeader;
	  private PlayersClient mPlayer;

    public void google_init(final int id){
      instanceId = id;

      Log.d(TAG,"Google init");

      GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;

      mClient = GoogleSignIn.getClient(activity,signInOptions);

      start();
    }

    public void start()
    {
      // Checking if an account is already connected:
      mAccount =  GoogleSignIn.getLastSignedInAccount(activity);

      if(mAccount == null)
      {
        //signInSilently();
        Log.d(TAG,"Starting connect()");
        connect();
      }
      else{
        Log.d(TAG,"Accounted logged in already");
        successSignIn();
      }
    }

    public void connect(){
      if (mAccount != null){
        Log.d(TAG,"Already connected");
        return;
      }
      Log.d(TAG,"Tries to sign in");
      Intent intent = mClient.getSignInIntent();
      activity.startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST);

    }

    public void disconnect(){
      mClient.signOut();
    }

    private void successSignIn(){
      Log.d(TAG,"Sign in :D");

      GodotLib.calldeferred(instanceId, "_on_successful_sign_in", new Object[]{ });

      mAchie = Games.getAchievementsClient(activity, mAccount);
      mLeader = Games.getLeaderboardsClient(activity, mAccount);
      mPlayer = Games.getPlayersClient(activity, mAccount);

      Games.getGamesClient(activity, mAccount).setViewForPopups(
		activity.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void signInSilently() {
		/*if (mAccount != null) { return; }

		GoogleSignInClient signInClient = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

		signInClient.silentSignIn().addOnCompleteListener(activity, new OnCompleteListener<GoogleSignInAccount>() {
			@Override
			public void onComplete(Task<GoogleSignInAccount> task) {
				if (task.isSuccessful()) {
					// The signed in account is stored in the task's result.
					try {
						mAccount = task.getResult(ApiException.class);
						successSignIn();
					} catch (ApiException e) {
						Log.w(TAG, "SignInResult::Failed code="
						+ e.getStatusCode()) ;
					}
				} else {
					// Player will need to sign-in explicitly using via UI
					Log.d(TAG, "Silent::Login::Failed");
          connect();
				}
			}
		});*/ return;
	}


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GOOGLE_SIGN_IN_REQUEST) {

			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

			if(result.isSuccess()){
        mAccount = result.getSignInAccount();
        successSignIn();
      }else{
        Status s = result.getStatus();

			Log.w(TAG, "SignInResult::Failed code=" + s.getStatusCode() + ", Message: " + s.getStatusMessage());
      GodotLib.calldeferred(instanceId, "_on_failed_sign_in", new Object[]{ });
      }
		}
	}

    public void achievementUnlock(final String a_id){
      if(mAccount != null)
      {
        mAchie.unlock(a_id);
        GodotLib.calldeferred(instanceId, "_on_achievement_unlock", new Object[]{a_id });
      }
    }
    public void achievementIncrease(final String a_id, final int amount){
      if(mAccount != null){
        mAchie.increment(a_id,amount);
        GodotLib.calldeferred(instanceId, "_on_achievement_increase", new Object[]{a_id,amount });
      }
    }
    public void achievementShowList(){
      if(mAccount != null){
        mAchie.getAchievementsIntent().addOnSuccessListener(new OnSuccessListener<Intent>() {
				@Override
				public void onSuccess(Intent intent) {
					activity.startActivityForResult(intent, REQUEST_ACHIEVEMENTS);
          GodotLib.calldeferred(instanceId, "_on_achievement_list_show", new Object[]{ });
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(Exception e) {
					Log.d(TAG, "Show Leaderboard failed:: " + e.toString());
          GodotLib.calldeferred(instanceId, "_on_achievement_list_failed", new Object[]{e.toString()});
				}
			});
      }
    }
    public void leaderSubmit(final String l_id,int score){
      if(mAccount != null){
        mLeader.submitScore(l_id,score);
        GodotLib.calldeferred(instanceId, "_on_leader_submit", new Object[]{ });
      }
    }
    public void leaderShow(final String l_id){
      if(mAccount != null){
        mLeader.getLeaderboardIntent(l_id).addOnSuccessListener(new OnSuccessListener<Intent>() {
				@Override
				public void onSuccess (Intent intent) {
					activity.startActivityForResult(intent, REQUEST_LEADERBOARD);
          GodotLib.calldeferred(instanceId, "_on_leader_show", new Object[]{ });
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(Exception e) {
					Log.d(TAG, "Showing Leaderboard failed: " + e.toString());
          GodotLib.calldeferred(instanceId, "_on_leader_failed", new Object[]{e.toString() });
				}
			});
      }
    }
    public void leaderShowList(){
      if(mAccount != null){
        mLeader.getAllLeaderboardsIntent().addOnSuccessListener(new OnSuccessListener<Intent>() {
				@Override
				public void onSuccess (Intent intent) {
					activity.startActivityForResult(intent, REQUEST_LEADERBOARD);
          GodotLib.calldeferred(instanceId, "_on_leader_show", new Object[]{ });
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(Exception e) {
					Log.d(TAG, "Showing Leader list failed: " + e.toString());
          GodotLib.calldeferred(instanceId, "_on_leader_failed", new Object[]{e.toString() });
				}
			});
      }
    }

    public void getInstanceId(int pInstanceId) {
        // You will need to call this method from Godot and pass in the get_instance_id().
        instanceId = pInstanceId;
    }

    static public Godot.SingletonBase initialize(Activity p_activity) {
        return new PlayGames(p_activity);
    }

    public PlayGames(Activity p_activity) {
        //register class name and functions to bind
        registerClass("PlayGames", new String[]
            {
                "getInstanceId",
                "google_init","start","connect","disconnect","signInSilently",
                "achievementUnlock","achievementIncrease","achievementShowList",
                "leaderShow","leaderSubmit","leaderShowList"
            });
        activity = p_activity;
        context = activity.getApplicationContext();


    }

    protected void onMainActivityResult(int requestCode, int resultCode, Intent data) {}
    protected void onMainRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {}

    protected void onMainPause() {}
    protected void onMainResume() {
      //signInSilently();
    }
    protected void onMainDestroy() {
      activity = null;
    }

    //protected void onGLDrawFrame(GL10 gl) {}
    //protected void onGLSurfaceChanged(GL10 gl, int width, int height) {} // singletons will always miss first onGLSurfaceChanged call

    // Some constants for use
    private static final int GOOGLE_SIGN_IN_REQUEST	= 9001;
  	private static final int REQUEST_ACHIEVEMENTS = 9002;
  	private static final int REQUEST_LEADERBOARD = 9003;


}
