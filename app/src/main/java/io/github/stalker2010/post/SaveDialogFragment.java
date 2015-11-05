package io.github.stalker2010.post;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.view.*;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import static io.github.stalker2010.post.PostApplication.*;
import android.widget.*;

public class SaveDialogFragment extends DialogFragment
{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		final View inflated = inflater.inflate(R.layout.save_dialog, null);
		builder.setView(inflated)
			// Add action buttons
			.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id)
				{
					EditText et = (EditText) inflated.findViewById(R.id.savedialogInput);
					String trim = et.getText().toString().trim();
					current().setFile(StorageUtils.get().fileByName(trim + ".post"));
					if (!current().saveToFile()) {
						Toast.makeText(getActivity(), "Failed to save to file", Toast.LENGTH_LONG).show();
					}
					SaveDialogFragment.this.getDialog().dismiss();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id)
				{
					SaveDialogFragment.this.getDialog().cancel();
				}
			});      
		return builder.create();
	}
}
