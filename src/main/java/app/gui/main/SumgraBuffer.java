package app.gui.main;

import org.apache.log4j.Logger;

import com.gs.collections.impl.list.mutable.FastList;

import app.gui.database.GraphDBView;

/**
 * @author Erick Cuenca
 *
 */
public class SumgraBuffer {

	public Logger logger = Logger.getLogger(SumgraBuffer.class);
	public static GraphDBView processingApp;
	private FastList<int[]> embeddings = new FastList<int[]>();
	private int bufferThreshold;
	private boolean available;
	private boolean stop;

	/**
	 * Default constructor
	 * 
	 * @param bufferThreshold
	 *            limit of the embedding list to fetch results
	 * 
	 */
	public SumgraBuffer(int bufferThreshold) {
		this.bufferThreshold = bufferThreshold;
	}

	/**
	 * Method to fill the embedding list from SuMGra results
	 * 
	 * @param embedding
	 */
	public synchronized void putOn(int[] embedding) {
		while (available) {
			try {
				// wait while embedding is full
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.embeddings.add(embedding);
		if (embeddings.size() == bufferThreshold) {
			available = true;
			notify();
		}
	}

	/**
	 * Method to pickUp the embedding list and update the heat map in the
	 * processingApp
	 */
	public synchronized void pickUp() {
		while (!available) {
			try {
				// wait while embedding empty (available=false)
				// wait while embedding is not size limit
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// CALL SYNCHRONIZED ONLY HEATMAP
		processingApp.fetchEmbeddings(embeddings);

		this.embeddings.clear();
		if ((this.embeddings.size() == 0)) {
			available = false;
			notify();
		}
	}

	/**
	 * Method to load the remaining embedding when the process is finish
	 */
	public synchronized void stopProcess() {
		processingApp.fetchEmbeddings(embeddings);
		processingApp.pauseSumgraProcess();
		processingApp.stopSumgraProcess();
		this.embeddings.clear();
		setStop(true);
	}

	public FastList<int[]> getListEmbeddings() {
		return embeddings;
	}

	public void setListEmbeddings(FastList<int[]> listEmbeddings) {
		this.embeddings = listEmbeddings;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getListThreshold() {
		return bufferThreshold;
	}

	public void setListThreshold(int listThreshold) {
		this.bufferThreshold = listThreshold;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean isStop) {
		this.stop = isStop;
	}

}
