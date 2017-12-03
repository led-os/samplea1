package com.jiubang.ggheart.common.password;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.SystemClock;

/**
 * 密码锁工具类
 * @author 
 *
 */
public class LockPatternUtils
{
	public static final int		LOCK_TYPE_NONE							= 0;
	public static final int		LOCK_TYPE_PATTERN						= 1;
	public static final int		LOCK_TYPE_NUMERIC						= 2;
	public static final int		LOCK_TYPE_ALPHABETIC					= 3;
	public static final int		LOCK_TYPE_ALPHANUMERIC					= 4;
	public static final int		LOCK_TYPE_GESTURE						= 5;
	public static final String	REQUEST_TYPE							= "lock_type";
	public static final int		REQUEST_TYPE_UNLOCK						= 101;
	public static final int		REQUEST_CLEARLOCK						= 102;
	public static final int		REQUEST_UNLOCK_OK						= 103;
	public static final int		REQUEST_SAVE_PASSWORD_OK				= 104;
	public static final int		OPTION_NONE								= 0;
	public static final int		OPTION_DIGIT							= 1;
	public static final int		OPTION_PATTERN							= 2;
	public static final String	DATA_CONFIGURATION						= "configuration";
	public static final String	DATA_LOCKTYPE							= "lock_type";
	public static final String	DATA_TOTALFAILEDATTEMPTS				= "totalFailedAttempts";
	public static final String	DATA_KEYGUARDSCREENCALLBACK				= "keyguardScreenCallback";
	private static final String	LOCK_PATTERN_FILE						= "/gesture.key";
	private static final String	LOCK_PASSWORD_FILE						= "/password.key";
	public static final int		FAILED_ATTEMPTS_BEFORE_TIMEOUT			= 5;
	public static final int		FAILED_ATTEMPTS_BEFORE_RESET			= 20;
	public static final long	FAILED_ATTEMPT_TIMEOUT_MS				= 30000L;
	public static final long	FAILED_ATTEMPT_COUNTDOWN_INTERVAL_MS	= 1000L;
	public static final int		MIN_LOCK_PATTERN_SIZE					= 4;
	public static final int		MIN_PATTERN_REGISTER_FAIL				= 3;
	public final static String	PASSWORD_TYPE_KEY						= "lockscreen.password_type";
	private static long			sDeadLine								= 0L;
	private static String		sLockPatternFilename					= null;
	private static String		sLockPasswordFilename					= null;
	public static final String	PREF_KEY_LOCK_TYPE						= "pref_key_lock_type";
	public static final int		KEYPADTYPE_NONE							= 0;
	public static final int		KEYPADTYPE_PASSWORD						= 1;
	public static final int		KEYPADTYPE_PATTERN						= 2;
	public static final int		KEYPASTYPRE_GESTURE						= 3;
	public static final String	LOCK_PASSWORD							= "password";
	//private static Dialog 			mDialog 						= null;
	public static int			sNumWrongConfirmAttempts				= 0;

	/**
	 * 初始化密码保存路径
	 * @param context
	 */
	public static void init(Context context)
	{
		if (context != null)
		{
			// 可能由于权限问题context.getFilesDir()出现null,因此加一个保护。
			try
			{
				if (sLockPatternFilename == null)
				{
					sLockPatternFilename = context.getFilesDir().getAbsolutePath()
							+ LOCK_PATTERN_FILE;
				}
				if (sLockPasswordFilename == null)
				{
					sLockPasswordFilename = context.getFilesDir().getAbsolutePath()
							+ LOCK_PASSWORD_FILE;
				}
			}
			catch (Exception e)
			{
			}
		}
	}

	/**
	 * Check to see if a pattern matches the saved pattern.  If no pattern exists,
	 * always returns true.
	 * @param pattern The pattern to check.
	 * @return Whether the pattern matches the stored one.
	 */
	public static boolean checkPattern(List<LockPatternView.Cell> pattern)
	{
		if (sLockPatternFilename == null)
		{
			return false;
		}
		try
		{
			// Read all the bytes from the file
			RandomAccessFile raf = new RandomAccessFile(sLockPatternFilename, "r");
			final byte[] stored = new byte[(int) raf.length()];
			int got = raf.read(stored, 0, stored.length);
			raf.close();
			if (got <= 0)
			{
				return true;
			}
			// Compare the hash from the file with the entered pattern's hash
			return Arrays.equals(stored, LockPatternUtils.patternToHash(pattern));
		}
		catch (FileNotFoundException fnfe)
		{
			return true;
		}
		catch (IOException ioe)
		{
			return true;
		}
	}

	/**
	 * Check to see if a password matches the saved password.  If no password exists,
	 * always returns true.
	 * @param password The password to check.
	 * @return Whether the password matches the stored one.
	 */
	public static boolean checkPassword(String password)
	{
		if (sLockPasswordFilename == null)
		{
			return false;
		}
		try
		{
			// Read all the bytes from the file
			RandomAccessFile raf = new RandomAccessFile(sLockPasswordFilename, "r");
			final byte[] stored = new byte[(int) raf.length()];
			int got = raf.read(stored, 0, stored.length);
			raf.close();
			if (got <= 0)
			{
				return true;
			}
			// Compare the hash from the file with the entered password's hash
			return Arrays.equals(stored, passwordToHash(password));
		}
		catch (FileNotFoundException fnfe)
		{
			return true;
		}
		catch (IOException ioe)
		{
			return true;
		}
	}

	/**
	 * Checks to see if the given file exists and contains any data. Returns true if it does,
	 * false otherwise.
	 * @param filename
	 * @return true if file exists and is non-empty.
	 */
	private static boolean nonEmptyFileExists(String filename)
	{
		if (filename == null)
		{
			return false;
		}
		try
		{
			// Check if we can read a byte from the file
			RandomAccessFile raf = new RandomAccessFile(filename, "r");
			raf.readByte();
			raf.close();
			return true;
		}
		catch (FileNotFoundException fnfe)
		{
			return false;
		}
		catch (Exception ioe)
		{
			return false;
		}
	}

	/**
	 * Check to see if the user has stored a lock pattern.
	 * @return Whether a saved pattern exists.
	 */
	public static boolean savedPatternExists()
	{
		return nonEmptyFileExists(sLockPatternFilename);
	}

	/**
	 * Check to see if the user has stored a lock pattern.
	 * @return Whether a saved pattern exists.
	 */
	public static boolean savedPasswordExists()
	{
		return nonEmptyFileExists(sLockPasswordFilename);
	}

	/**
	 * Clear any lock pattern or password.
	 */
	public static void clearLock()
	{
		saveLockPassword(null, 0);
		saveLockPattern(null);
	}

	/**
	 * Save a lock pattern.
	 * @param pattern The new pattern to save.
	 */
	public static void saveLockPattern(List<LockPatternView.Cell> pattern)
	{
		if (sLockPatternFilename == null)
		{
			return;
		}
		// Compute the hash
		final byte[] hash = LockPatternUtils.patternToHash(pattern);
		try
		{
			// Write the hash to file
			RandomAccessFile raf = new RandomAccessFile(sLockPatternFilename, "rw");
			// Truncate the file if pattern is null, to clear the lock
			if (pattern == null)
			{
				raf.setLength(0);
			}
			else
			{
				raf.write(hash, 0, hash.length);
			}
			raf.close();
		}
		catch (FileNotFoundException fnfe)
		{
			// Cant do much, unless we want to fail over to using the settings provider
			//Log.e(TAG, "Unable to save lock pattern to " + sLockPatternFilename);
		}
		catch (IOException ioe)
		{
			// Cant do much
			//Log.e(TAG, "Unable to save lock pattern to " + sLockPatternFilename);
		}
	}

	/**
	 * Save a lock password.  Does not ensure that the password is as good
	 * as the requested mode, but will adjust the mode to be as good as the
	 * pattern.
	 * @param password The password to save
	 * @param quality {@see DevicePolicyManager#getPasswordQuality(android.content.ComponentName)}
	 */
	public static void saveLockPassword(String password, int quality)
	{
		if (sLockPasswordFilename == null)
		{
			return;
		}
		// Compute the hash
		final byte[] hash = passwordToHash(password);
		try
		{
			// Write the hash to file
			RandomAccessFile raf = new RandomAccessFile(sLockPasswordFilename, "rw");
			// Truncate the file if pattern is null, to clear the lock
			if (password == null)
			{
				raf.setLength(0);
			}
			else
			{
				raf.write(hash, 0, hash.length);
			}
			raf.close();
		}
		catch (FileNotFoundException fnfe)
		{
			// Cant do much, unless we want to fail over to using the settings provider
			//Log.e(TAG, "Unable to save lock pattern to " + sLockPasswordFilename);
		}
		catch (IOException ioe)
		{
			// Cant do much
			//Log.e(TAG, "Unable to save lock pattern to " + sLockPasswordFilename);
		}
	}

	/**
	 * Deserialize a pattern.
	 * @param string The pattern serialized with {@link #patternToString}
	 * @return The pattern.
	 */
	public static List<LockPatternView.Cell> stringToPattern(String string)
	{
		List<LockPatternView.Cell> result = new ArrayList<LockPatternView.Cell>();

		final byte[] bytes = string.getBytes();
		for (int i = 0; i < bytes.length; i++)
		{
			byte b = bytes[i];
			result.add(LockPatternView.Cell.of(b / 3, b % 3));
		}

		return result;
	}

	/**
	 * Serialize a pattern.
	 * @param pattern The pattern.
	 * @return The pattern in string form.
	 */
	public static String patternToString(List<LockPatternView.Cell> pattern)
	{
		if (pattern == null)
		{
			return "";
		}
		final int patternSize = pattern.size();

		byte[] res = new byte[patternSize];
		for (int i = 0; i < patternSize; i++)
		{
			LockPatternView.Cell cell = pattern.get(i);
			res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
		}

		return new String(res);
	}

	/*
	 * Generate an SHA-1 hash for the pattern. Not the most secure, but it is
	 * at least a second level of protection. First level is that the file
	 * is in a location only readable by the system process.
	 * @param pattern the gesture pattern.
	 * @return the hash of the pattern in a byte array.
	 */
	private static byte[] patternToHash(List<LockPatternView.Cell> pattern)
	{
		if (pattern == null)
		{
			return null;
		}

		final int patternSize = pattern.size();
		byte[] res = new byte[patternSize];
		for (int i = 0; i < patternSize; i++)
		{
			LockPatternView.Cell cell = pattern.get(i);
			res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
		}
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] hash = md.digest(res);
			return hash;
		}
		catch (NoSuchAlgorithmException nsa)
		{
			return res;
		}
	}

	private static String getSalt()
	{
		long salt = 0x67452301;
		return Long.toHexString(salt);
	}

	/*
	 * Generate a hash for the given password. To avoid brute force attacks, we use a salted hash.
	 * Not the most secure, but it is at least a second level of protection. First level is that
	 * the file is in a location only readable by the system process.
	 * @param password the gesture pattern.
	 * @return the hash of the pattern in a byte array.
	 */
	public static byte[] passwordToHash(String password)
	{
		if (password == null)
		{
			return null;
		}
		// String algo = null;
		byte[] hashed = null;
		try
		{
			byte[] saltedPassword = (password + getSalt()).getBytes();
			byte[] sha1 = MessageDigest.getInstance("SHA-1").digest(saltedPassword);
			byte[] md5 = MessageDigest.getInstance("MD5").digest(saltedPassword);
			hashed = (toHex(sha1) + toHex(md5)).getBytes();
		}
		catch (NoSuchAlgorithmException e)
		{
			//Log.w(TAG, "Failed to encode string because of missing algorithm: " + algo);
		}
		return hashed;
	}

	private static String toHex(byte[] ary)
	{
		final String hex = "0123456789ABCDEF";
		String ret = "";
		for (int i = 0; i < ary.length; i++)
		{
			ret += hex.charAt((ary[i] >> 4) & 0xf);
			ret += hex.charAt(ary[i] & 0xf);
		}

		return ret;
	}

	/**
	 * Set and store the lockout deadline, meaning the user can't attempt his/her unlock
	 * pattern until the deadline has passed.
	 * @return the chosen deadline.
	 */
	public static long setLockoutAttemptDeadline()
	{
		final long deadline = SystemClock.elapsedRealtime() + FAILED_ATTEMPT_TIMEOUT_MS;
		sDeadLine = deadline;
		return deadline;
	}

	/**
	 * @return The elapsed time in millis in the future when the user is allowed to
	 *   attempt to enter his/her lock pattern, or 0 if the user is welcome to
	 *   enter a pattern.
	 */
	public static long getLockoutAttemptDeadline()
	{
		final long deadline = sDeadLine;;
		final long now = SystemClock.elapsedRealtime();
		if (deadline < now || deadline > (now + FAILED_ATTEMPT_TIMEOUT_MS))
		{
			return 0L;
		}

		return deadline;
	}
}
