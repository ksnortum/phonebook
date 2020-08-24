package phonebook;

public class HashTable<T> {
	private static class TableEntry<T> {
		private final String key;
		private final T value;

		public TableEntry(String key, T value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public T getValue() {
			return value;
		}
	}

	private static final int SCALING = 2;

	private final int size;
	private final TableEntry[] table;
	
	public HashTable(int size) {
		this.size = size * SCALING;
		table = new TableEntry[this.size];
	}
	
	private int findEntryIndex(String key) {
		int stringHash = getHash(key);
		int hash = stringHash % size;

		while (table[hash] != null && !table[hash].getKey().equals(key)) {
		    hash = (hash + 1) % size;

		    if (hash == stringHash % size) {
		        return -1;
		    }
		}

		return hash;
	}

	private int getHash(String key) {
		int hash = 7;

		for (int i = 0; i < key.length(); i++) {
			hash = hash * 31 + key.charAt(i);
		}

		return Math.abs(hash);
	}
	
	public boolean put(String key, T value) {
		int idx = findEntryIndex(key);

		if (idx == -1) {
		    return false;
		}

		table[idx] = new TableEntry(key, value);
		return true;
	}
	
	public T get(String key) {
		int idx = findEntryIndex(key);

		if (idx == -1 || table[idx] == null) {
		    return null;
		}

		return (T) table[idx].getValue();
	}
}
