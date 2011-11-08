package jp.ac.jaist.skdlab.nvcsys;

import java.util.Collections;
import java.util.List;

/**
 * The non-verbal communication operating algorithm.
 * Based on algo10-29.c
 * 
 * @author Ren Sasaki, Yutaka Kato
 * @version 0.0.1
 */
public class NVCOperation {
	
	/** Remote communicator's name (identify use only) */
	public static final String REMOTE_NAME = "Remote";
	
	/** Singleton instance */
	private static NVCOperation instance = null;
	
	/** Current talking communicator's name */
	private String currentTalkingCommunicator = "nobody";

	/** Previous talked communicator's name */
	private String previousTalkedCommunicator = "nobody";
	
	/** State flag of signed/unsigned */
	private boolean signed = false;

	/**
	 *  Constructor is private, use getInstance() method.
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
	 * Update talking communicator.
	 * 
	 * @param name Current talking communicator
	 */
	public void setCurrentTalkingCommunicator(String name) {
		
		// Change communicator variables
		previousTalkedCommunicator = currentTalkingCommunicator;
		currentTalkingCommunicator = name;
		
		// Debug message
		System.out.println("[NVCOperation] Now talking: " + name);
	}
	
	/**
	 * Gestured by remote communicator
	 */
	public void gestured() {
		
		// Local communicator is now talking
		if (!currentTalkingCommunicator.equals(REMOTE_NAME)) {
			
			// State of not signed
			if (!signed) {
				
				// Second time in a row
				if (previousTalkedCommunicator.equals(
						currentTalkingCommunicator)) {
					
					// Randomize
					previousTalkedCommunicator = 
							random(previousTalkedCommunicator);
				}
				
				sign(true, previousTalkedCommunicator);
				signed = true;
			}
		}
		
		// Otherwise
		else {
			if (!signed) {
				sign(false, null);
				signed = false;
			}
		}
	}
	
	/**
	 * Hand gestured by remote communicator
	 */
	public void handGestured() {
		
		// Remote communicator is now talking
		if (currentTalkingCommunicator.equals(REMOTE_NAME)) {
			if (!signed) {
				sign(true, null);
				signed = true;
			}
		}
		
		// Otherwise
		else {
			if (!signed) {
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
	 * @param turnedOn up/down
	 */
	private void sign(boolean turnedOn) {
		
		if (turnedOn) {
			NVCClient.getInstance().sendMessage("UP_ALL");
		} else {
			NVCClient.getInstance().sendMessage("DOWN_ALL");
		}
	}
	
	/**
	 * Send sign to a communicator
	 * 
	 * @param turnedOn up/down
	 * @param target Target communicator's name
	 * @throws IllegalArgumentException If target missing
	 */
	private void sign(boolean turnedOn, String target) 
			throws IllegalArgumentException {
		
		if (target == null) {
			sign(turnedOn);
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
	 * Choose communicator by random, but except current talking communicator.
	 * 
	 * @param excepts Current talking communicator's name
	 * @return Chosen communicator's name
	 */
	private String random(String excepts) {
		
		List<String> temporaryMemberList = 
				NVCClient.getInstance().getDiscussionUserList();
		
		// Remove remote communicator
		if (temporaryMemberList.contains(REMOTE_NAME)) {
			temporaryMemberList.remove(REMOTE_NAME);
		}
		
		// Remove operator
		if (temporaryMemberList.contains(NVCClient.name)) {
			temporaryMemberList.remove(NVCClient.name);
		}
		
		// Remote current talking communicator
		if (temporaryMemberList.contains(excepts)) {
			temporaryMemberList.remove(excepts);
		}
		
		if (temporaryMemberList.size() == 0) {
			return "";
		}
		
		// Shuffle candidates
		Collections.shuffle(temporaryMemberList);
		
		// Return top item of shuffled list
		return temporaryMemberList.get(0);
	}
}
