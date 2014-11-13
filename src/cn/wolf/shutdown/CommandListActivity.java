package cn.wolf.shutdown;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * An activity representing a list of Commands. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link CommandDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link CommandListFragment} and the item details (if present) is a
 * {@link CommandDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link CommandListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class CommandListActivity extends Activity implements
		CommandListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_command_list);

		if (findViewById(R.id.command_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((CommandListFragment) getFragmentManager().findFragmentById(
					R.id.command_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.

		doMyTest();
	}

	private void doMyTest() {

		// 1） 设置自动关机的alarm：
		AlarmManager am = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(
				"com.android.settings.action.REQUEST_POWER_OFF");

		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, 1000, pendingIntent);

		// 2）自动关机调的是./frameworks/base/services/java/com/android/server/ShutdownActivity.java：
		Intent newIntent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(newIntent);

	}

	/**
	 * Callback method from {@link CommandListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(CommandDetailFragment.ARG_ITEM_ID, id);
			CommandDetailFragment fragment = new CommandDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.command_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, CommandDetailActivity.class);
			detailIntent.putExtra(CommandDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
