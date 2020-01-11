package israel13.androidtv.Utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by puspak on 2017.11.30.
 */

public class VerifyUtil {

	public static boolean isValidName(CharSequence name) {
		String REGEXP = "^[a-zA-Z\\-\\u0590-\\u05FF ]+$";
		Pattern pattern = Pattern.compile(REGEXP, Pattern.UNICODE_CASE);
		return pattern.matcher(name).matches();
	}

	public static boolean isValidEmail(CharSequence email) {
		return Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	public static boolean isValidPassword(String password) {
		String REGEXP = "^([a-zA-Z0-9]+)$";
		Pattern pattern = Pattern.compile(REGEXP, Pattern.UNICODE_CASE);
		return pattern.matcher(password).matches();

	}
	public static boolean isValidPhoneNumber(String phone) {
		return Patterns.PHONE.matcher(phone).matches();
	}

	public static InputFilter spaceInputFilter() {
		InputFilter filter = new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				for (int i = start; i < end; i++) {
					if (Character.isWhitespace(source.charAt(i))) return "";
				}
				return null;
			}
		};
		return filter;
	}
}
