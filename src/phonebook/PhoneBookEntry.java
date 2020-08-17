package phonebook;

public class PhoneBookEntry {
    private final String phoneNumber;
    private final String name;

    public PhoneBookEntry(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }
}
