package es.iessaladillo.pedrojoya.pr05.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import es.iessaladillo.pedrojoya.pr05.R;
import es.iessaladillo.pedrojoya.pr05.data.local.Database;
import es.iessaladillo.pedrojoya.pr05.data.local.model.User;
import es.iessaladillo.pedrojoya.pr05.ui.avatar.AvatarActivity;
import es.iessaladillo.pedrojoya.pr05.utils.KeyboardUtils;
import es.iessaladillo.pedrojoya.pr05.utils.ValidationUtils;

@SuppressWarnings("WeakerAccess")
public class ProfileActivity extends AppCompatActivity {

    private final Database database = Database.getInstance();
    private ImageView imgAvatar;
    private TextView lblAvatar;
    private EditText txtName;
    private TextView lblName;
    private EditText txtEmail;
    private TextView lblEmail;
    private EditText txtPhonenumber;
    private TextView lblPhonenumber;
    private EditText txtAddress;
    private TextView lblAddress;
    private EditText txtWeb;
    private TextView lblWeb;
    private ImageView imgPhonenumber;
    private ImageView imgEmail;
    private ImageView imgWeb;
    private ImageView imgAddress;
    private ConstraintLayout constraitLayout;

    User user;

    private static final int RC_AVATAR = 1;

    public static final String EXTRA_USER = "EXTRA_USER";

    private ProfileActivityViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        viewModel = ViewModelProviders.of(this).get(ProfileActivityViewModel.class);
        getIntentData(getIntent());
        initViews();
        validateFields();
        if(user != null) {
            setUser(user);
        }
    }

    private void setUser(User user) {
        txtName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        txtEmail.setText(user.getEmail());
        txtPhonenumber.setText(user.getPhonenumber());
        txtAddress.setText(user.getAddress());
        txtWeb.setText(user.getWeb());
        imgAvatar.setImageResource(user.getAvatar().getImageResId());
    }


    private void validateFields() {
        lblName.setEnabled(viewModel.isStateName());
        lblEmail.setEnabled(viewModel.isStateEmail());
        lblPhonenumber.setEnabled(viewModel.isStatePhonenumber());
        lblAddress.setEnabled(viewModel.isStateAddress());
        lblWeb.setEnabled(viewModel.isStateWeb());

        imgEmail.setEnabled(viewModel.isStateImgEmail());
        imgPhonenumber.setEnabled(viewModel.isStateImgPhonenumber());
        imgAddress.setEnabled(viewModel.isStateImgAddress());
        imgWeb.setEnabled(viewModel.isStateImgWeb());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuSave) {
            save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        if (!validateAll()) {
            Snackbar.make(constraitLayout, R.string.main_error_saving, Snackbar.LENGTH_LONG).show();
        } else {
            buildResult();
            finish();
        }
        KeyboardUtils.hideSoftKeyboard(this);
    }

    private void initViews() {
        imgAvatar = ActivityCompat.requireViewById(this, R.id.imgAvatar);
        lblAvatar = ActivityCompat.requireViewById(this, R.id.lblAvatar);
        txtName = ActivityCompat.requireViewById(this, R.id.txtName);
        lblName = ActivityCompat.requireViewById(this, R.id.lblName);
        txtEmail = ActivityCompat.requireViewById(this, R.id.txtEmail);
        lblEmail = ActivityCompat.requireViewById(this, R.id.lblEmail);
        txtPhonenumber = ActivityCompat.requireViewById(this, R.id.txtPhonenumber);
        lblPhonenumber = ActivityCompat.requireViewById(this, R.id.lblPhonenumber);
        txtAddress = ActivityCompat.requireViewById(this, R.id.txtAddress);
        lblAddress = ActivityCompat.requireViewById(this, R.id.lblAddress);
        txtWeb = ActivityCompat.requireViewById(this, R.id.txtWeb);
        lblWeb = ActivityCompat.requireViewById(this, R.id.lblWeb);
        imgPhonenumber = ActivityCompat.requireViewById(this, R.id.imgPhonenumber);
        imgEmail = ActivityCompat.requireViewById(this, R.id.imgEmail);
        imgWeb = ActivityCompat.requireViewById(this, R.id.imgWeb);
        imgAddress = ActivityCompat.requireViewById(this, R.id.imgAddress);
        constraitLayout = ActivityCompat.requireViewById(this, R.id.clProfile);

        viewModel.setDefaultAvatar();
        setProfileAvatar();

        //Avatar image listener
        View.OnClickListener avatarListener = v -> AvatarActivity.startForResult(ProfileActivity.this, RC_AVATAR, viewModel.getAvatar());

        imgAvatar.setOnClickListener(avatarListener);
        lblAvatar.setOnClickListener(avatarListener);

        //Label is bold when editText has focus
        txtName.setOnFocusChangeListener((v, hasFocus) -> setBold(txtName, lblName));
        txtEmail.setOnFocusChangeListener((v, hasFocus) -> setBold(txtEmail, lblEmail));
        txtPhonenumber.setOnFocusChangeListener((v, hasFocus) -> setBold(txtPhonenumber, lblPhonenumber));
        txtAddress.setOnFocusChangeListener((v, hasFocus) -> setBold(txtAddress, lblAddress));
        txtWeb.setOnFocusChangeListener((v, hasFocus) -> setBold(txtWeb, lblWeb));

        //SetTag
        imgPhonenumber.setTag(R.drawable.ic_call_24dp);
        imgEmail.setTag(R.drawable.ic_email_24dp);
        imgWeb.setTag(R.drawable.ic_web_24dp);
        imgAddress.setTag(R.drawable.ic_map_24dp);
        imgAvatar.setTag(database.getDefaultAvatar().getImageResId());
        lblAvatar.setTag(database.getDefaultAvatar().getName());

        //TextWatcher, check fields
        GestorTextWatcher gestorTextWatcher = new GestorTextWatcher();

        txtName.addTextChangedListener(gestorTextWatcher);
        txtEmail.addTextChangedListener(gestorTextWatcher);
        txtPhonenumber.addTextChangedListener(gestorTextWatcher);
        txtAddress.addTextChangedListener(gestorTextWatcher);
        txtWeb.addTextChangedListener(gestorTextWatcher);

        //Keuboard editorAction
        txtWeb.setOnEditorActionListener((v, actionId, event) -> {
            save();
            return false;
        });

        //Icons listener, send implicit intent
        View.OnClickListener imgListener = this::sendIntent;

        imgEmail.setOnClickListener(imgListener);
        imgPhonenumber.setOnClickListener(imgListener);
        imgAddress.setOnClickListener(imgListener);
        imgWeb.setOnClickListener(imgListener);
    }

    private void sendIntent(View v) {
        if (v.getId() == imgEmail.getId()) {
            sendEmail();
        } else if (v.getId() == imgPhonenumber.getId()) {
            callPhone();
        } else if (v.getId() == imgAddress.getId()) {
            searchAddress();
        } else if (v.getId() == imgWeb.getId()) {
            viewWeb();
        }
    }

    //Intent del Main a Profile
    public static void startForResult(Activity activity, int requestCode, User user) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        if(user != null) {
            intent.putExtra(EXTRA_USER, user);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    private void getIntentData(Intent intent) {
        if(intent != null && intent.hasExtra(EXTRA_USER)) {
            if(intent.getParcelableExtra(EXTRA_USER) != null) {
                user = intent.getParcelableExtra(EXTRA_USER);
            }
        }
    }

    private void buildResult() {
        Intent intent = new Intent();
        asignUser();
        intent.putExtra(EXTRA_USER, user);
        this.setResult(RESULT_OK, intent);
    }

    private void asignUser() {
        //Edit
        if(user != null) {
            user.setName(txtName.getText().toString());
            user.setEmail(txtEmail.getText().toString());
            user.setPhonenumber(txtPhonenumber.getText().toString());
            user.setAddress(txtAddress.getText().toString());
            user.setWeb(txtWeb.getText().toString());
            user.setAvatar(viewModel.getAvatar());
        } else { //Add
            user = new User();
            user.setName(txtName.getText().toString());
            user.setEmail(txtEmail.getText().toString());
            user.setPhonenumber(txtPhonenumber.getText().toString());
            user.setAddress(txtAddress.getText().toString());
            user.setWeb(txtWeb.getText().toString());
            user.setAvatar(viewModel.getAvatar());
        }
    }
    //Intent del Main a Profile

    private void setBold(EditText editText, TextView label) {
        if(editText.hasFocus()) {
            label.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            label.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }

    //Intent Profile - Avatar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == RC_AVATAR) {
            obtainResponseData(data);
        }
    }

    private void obtainResponseData(Intent intent) {
        if (intent != null && intent.hasExtra(AvatarActivity.EXTRA_AVATAR)) {
            viewModel.setAvatar(intent.getParcelableExtra(AvatarActivity.EXTRA_AVATAR));
        }
        setProfileAvatar();
    }
    //Intent Profile - Avatar

    private void setProfileAvatar() {
        imgAvatar.setImageResource(viewModel.getAvatar().getImageResId());
        lblAvatar.setText(viewModel.getAvatar().getName());
        imgAvatar.setTag(viewModel.getAvatar().getImageResId());
    }

    //Implicit Intents
    private void sendEmail() {
        String email = txtEmail.getText().toString();
        Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(String.format("mailto:%s", email)));
        startActivity(sendEmailIntent);
    }

    private void callPhone() {
        String phone = txtPhonenumber.getText().toString();
        Intent callPhoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(String.format("tel:%s", phone)));
        startActivity(callPhoneIntent);
    }

    private void searchAddress() {
        String address = txtAddress.getText().toString();
        Intent searchAddressIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("geo:0,0?q=%s", address)));
        startActivity(searchAddressIntent);
    }

    private void viewWeb() {
        String web = txtWeb.getText().toString();
        if (!web.startsWith("http://")) {
            web = String.format("http://%s", web);
        }
        Intent viewWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(web));
        startActivity(viewWebIntent);
    }

    private class GestorTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkCurrentView();
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkCurrentView();
        }
    }

    //Validation to enable/disable icons/labels
    private void checkName() {
        enabledDisabledField(txtName, lblName, ValidationUtils.isValidName(txtName.getText().toString()));
    }

    private void checkEmail() {
        enabledDisabledFieldImg(txtEmail, imgEmail, lblEmail, ValidationUtils.isValidEmail(txtEmail.getText().toString()));
    }

    private void checkPhonenumber() {
        enabledDisabledFieldImg(txtPhonenumber, imgPhonenumber, lblPhonenumber, ValidationUtils.isValidPhone(txtPhonenumber.getText().toString()));
    }

    private void checkAddress() {
        enabledDisabledFieldImg(txtAddress, imgAddress, lblAddress, ValidationUtils.isValidAddress(txtAddress.getText().toString()));
    }

    private void checkWeb() {
        enabledDisabledFieldImg(txtWeb, imgWeb, lblWeb, ValidationUtils.isValidUrl(txtWeb.getText().toString()));
    }

    private void checkCurrentView() {
        if(getCurrentFocus() != null) {
            if (getCurrentFocus().getId() == txtName.getId()) {
                checkName();
            } else if (getCurrentFocus().getId() == txtEmail.getId()) {
                checkEmail();
            } else if (getCurrentFocus().getId() == txtPhonenumber.getId()) {
                checkPhonenumber();
            } else if (getCurrentFocus().getId() == txtAddress.getId()) {
                checkAddress();
            } else if (getCurrentFocus().getId() == txtWeb.getId()) {
                checkWeb();
            }
        }
    }

    private void checkAll() {
        checkName();
        checkEmail();
        checkPhonenumber();
        checkAddress();
        checkWeb();
    }

    private boolean validateAll() {
        checkAll();
        View[] array = new View[]{lblName, lblEmail, lblPhonenumber, lblAddress, lblWeb};
        for (View view: array) {
            if(!view.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    private void enabledDisabledFieldImg(EditText editText, ImageView imageView, TextView textView, boolean valid) {
        if(valid) {
            editText.setError(null);
        } else {
            editText.setError(getString(R.string.main_invalid_data));
        }
        imageView.setEnabled(valid);
        textView.setEnabled(valid);
        selectStateView(textView, valid);
    }

    //For name field, field has not icon
    private void enabledDisabledField(EditText editText, TextView textView, boolean valid) {
        if(valid) {
            editText.setError(null);
        } else {
            editText.setError(getString(R.string.main_invalid_data));
        }
        textView.setEnabled(valid);
        selectStateView(textView, valid);
    }

    //Save states
    private void selectStateView(View view, boolean state) {
        if(view == lblName) {
            viewModel.setStateName(state);
        } else if(view == lblEmail) {
            viewModel.setStateEmail(state);
            viewModel.setStateImgEmail(state);
        } else if(view == lblPhonenumber) {
            viewModel.setStatePhonenumber(state);
            viewModel.setStateImgPhonenumber(state);
        } else if(view == lblAddress) {
            viewModel.setStateAddress(state);
            viewModel.setStateImgAddress(state);
        } else if(view == lblWeb) {
            viewModel.setStateWeb(state);
            viewModel.setStateImgWeb(state);
        }
    }
}
