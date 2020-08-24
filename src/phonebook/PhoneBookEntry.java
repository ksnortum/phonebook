package phonebook;

public class PhoneBookEntry {
    private final int phoneNumber;
    private final String name;

    public PhoneBookEntry(int phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }
}
