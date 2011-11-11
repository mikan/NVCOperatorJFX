package jp.ac.jaist.skdlab.nvcsys;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The non-verbal communication operating algorithm. Based on algo10-29.c
 * 
 * @author Ren Sasaki, Yutaka Kato
 * @version 0.0.3
 */
public class NVCOperation {

	/** Remote member's name (identify use only) */
	public static final String REMOTE_NAME = "Remote";

	/** Singleton instance */
	private static NVCOperation instance = null;

	/** Current talking member's name */
	private String currentTalkingmember = "nobody";

	/** Previous talked member's name */
	private String previousTalkedmember = "nobody";

	/** State flag of signed/unsigned */
	private boolean signed = false;

	/**
	 * Constructor is private, use getInstance() method.
	 */
	private NVCOperation() {
		// do nothing on constructor
	}

	/**
	 * Get NVCOperation's singleton instance.
	 * 
	 * @return NVCOperation instance
	 */
	public static NVCOperation getInstance() {
		if (instance == null) {
			instance = new NVCOperation();
		}
		return instance;
	}

	/**
	 * Update talking member.
	 * 
	 * @param name
	 *            Current talking member
	 */
	public void setCurrentTalkingMember(String name) {

		// Change member variables
		previousTalkedmember = currentTalkingmember;
		currentTalkingmember = name;

		// Debug message
		System.out.println("[NVCOperation] Now talking: " + name);
	}

	/**
	 * Gestured by remote member
	 */
	public void gestured() {

		// Local member is now talking
		if (!currentTalkingmember.equals(REMOTE_NAME)) {

			if (signed) {
				sign(false, null);
				signed = false;
			}

			// Second time in a row
			if (previousTalkedmember.equals(currentTalkingmember)) {
				sign(true, random(currentTalkingmember));
				signed = true;
			}

			// Send to previous member
			else {
				sign(true, previousTalkedmember);
				signed = true;
			}
		}

		// Otherwise
		else {
			if (signed) {
				sign(false, null);
				signed = false;
			}
		}
	}

	/**
	 * Hand gestured by remote member
	 */
	public void handGestured() {

		// Remote member is now talking
		if (currentTalkingmember.equals(REMOTE_NAME)) {

			if (signed) {
				sign(false, null);
				signed = false;
			}

			sign(true, null);
			signed = true;
		}

		// Otherwise
		else {
			if (signed) {
				sign(false, null);
				signed = false;
			}
		}
	}

	/**
	 * Send "DOWN_ALL" by manually.
	 */
	public void turnOffAllManually() {
		NVCClient.getInstance().sendMessage("DOWN_ALL");
		signed = false;
	}

	/**
	 * Send sign to all.
	 * 
	 * @param turnedOn
	 *            up/down
	 */
	private void sign(boolean turnedOn) {

		if (turnedOn) {
			NVCClient.getInstance().sendMessage("UP_ALL");
		} else {
			NVCClient.getInstance().sendMessage("DOWN_ALL");
		}
	}

	/**
	 * Send sign to a member
	 * 
	 * @param turnedOn
	 *            up/down
	 * @param target
	 *            Target member's name
	 * @throws IllegalArgumentException
	 *             If target missing
	 */
	private void sign(boolean turnedOn, String target)
			throws IllegalArgumentException {

		if (target == null) {
			sign(turnedOn);
			return;
		}

		if (target == REMOTE_NAME) {
			// Don't send sign to remote
			return;
		}

		if (!NVCClient.getInstance().getDiscussionUserList().contains(target)
				&& target != "") {
			throw new IllegalArgumentException("Missing target");
		}

		if (turnedOn) {
			NVCClient.getInstance().sendMessage("UP " + target);
		} else {
			NVCClient.getInstance().sendMessage("DOWN_ALL");
		}
	}

	/**
	 * Choose member by random, but except current talking member.
	 * 
	 * @param excepts
	 *            Current talking member's name
	 * @return Chosen member's name
	 */
	private String random(String excepts) {

		List<String> memberList = NVCClient.getInstance()
				.getDiscussionUserList();
		List<String> candidateList = new ArrayList<String>();

		for (String s : memberList) {
			if (!s.equals(REMOTE_NAME) && // Remote
					!s.equals(NVCClient.name) && // Operator
					!s.equals(excepts)) { // Current talking member
				candidateList.add(s);
			}
		}

		if (candidateList.size() == 0) {
			return "";
		}

		long seed = System.currentTimeMillis();
		return candidateList
				.get(new Random(seed).nextInt(candidateList.size()));
	}
}
