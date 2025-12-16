import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

enum RoomType {
    STANDARD_SUITE,
    JUNIOR_SUITE,
    MASTER_SUITE
}

class User {
    int userId;
    int balance;

    public User(int userId, int balance) {
        this.userId = userId;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User[ID=" + userId + ", Balance=" + balance + "]";
    }
}

class Room {
    int roomNumber;
    RoomType roomType;
    int pricePerNight;

    public Room(int roomNumber, RoomType roomType, int pricePerNight) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
    }

    @Override
    public String toString() {
        return "Room[Number=" + roomNumber + ", Type=" + roomType + ", Price/Night=" + pricePerNight + "]";
    }
}

class Booking {
    User user;
    Room room;
    Date checkIn;
    Date checkOut;
    int totalPrice;

    public Booking(User user, Room room, Date checkIn, Date checkOut) {
        this.user = user;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPrice = calculateTotalPrice();
    }

    private int calculateTotalPrice() {
        long diff = checkOut.getTime() - checkIn.getTime();
        int nights = (int)(diff / (1000 * 60 * 60 * 24));
        return nights * room.pricePerNight;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "Booking[UserID=" + user.userId +
                ", RoomNumber=" + room.roomNumber +
                ", Type=" + room.roomType +
                ", CheckIn=" + sdf.format(checkIn) +
                ", CheckOut=" + sdf.format(checkOut) +
                ", TotalPrice=" + totalPrice + "]";
    }
}

class Service {
    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<User> users = new ArrayList<>();
    ArrayList<Booking> bookings = new ArrayList<>();

    void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        for (Room r : rooms) {
            if (r.roomNumber == roomNumber) {
                // Si la chambre existe déjà, mettre à jour uniquement le type et prix
                r.roomType = roomType;
                r.pricePerNight = roomPricePerNight;
                return;
            }
        }
        rooms.add(new Room(roomNumber, roomType, roomPricePerNight));
    }

    void setUser(int userId, int balance) {
        for (User u : users) {
            if (u.userId == userId) return; // déjà existant
        }
        users.add(new User(userId, balance));
    }

    void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {
        try {
            if (!checkIn.before(checkOut)) {
                System.out.println("Erreur : la date de check-in doit être avant la date de check-out.");
                return;
            }

            User user = null;
            Room room = null;

            for (User u : users) if (u.userId == userId) user = u;
            for (Room r : rooms) if (r.roomNumber == roomNumber) room = r;

            if (user == null || room == null) {
                System.out.println("Erreur : Utilisateur ou chambre introuvable.");
                return;
            }

            for (Booking b : bookings) {
                if (b.room.roomNumber == roomNumber) {
                    if (!(checkOut.before(b.checkIn) || checkIn.after(b.checkOut))) {
                        System.out.println("Erreur : Chambre " + roomNumber + " déjà réservée pendant cette période.");
                        return;
                    }
                }
            }

            long diff = checkOut.getTime() - checkIn.getTime();
            int nights = (int)(diff / (1000 * 60 * 60 * 24));
            int totalPrice = nights * room.pricePerNight;

            if (user.balance < totalPrice) {
                System.out.println("Erreur : Solde insuffisant pour réserver cette chambre.");
                return;
            }

            user.balance -= totalPrice;
            Booking booking = new Booking(user, room, checkIn, checkOut);
            bookings.add(booking);
            System.out.println("Réservation réussie : " + booking);

        } catch (Exception e) {
            System.out.println("Erreur lors de la réservation : " + e.getMessage());
        }
    }

    void printAll() {
        System.out.println("\n--- Toutes les chambres ---");
        for (int i = rooms.size() - 1; i >= 0; i--) System.out.println(rooms.get(i));

        System.out.println("\n--- Toutes les réservations ---");
        for (int i = bookings.size() - 1; i >= 0; i--) System.out.println(bookings.get(i));
    }

    void printAllUsers() {
        System.out.println("\n--- Tous les utilisateurs ---");
        for (int i = users.size() - 1; i >= 0; i--) System.out.println(users.get(i));
    }
}


public class Main {
    public static void main(String[] args) throws Exception {
        Service service = new Service();

        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        service.setRoom(2, RoomType.JUNIOR_SUITE, 2000);
        service.setRoom(3, RoomType.MASTER_SUITE, 3000);

        service.setUser(1, 5000);
        service.setUser(2, 10000);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        service.bookRoom(1, 2, sdf.parse("30/06/2026"), sdf.parse("07/07/2026")); // OK
        service.bookRoom(1, 2, sdf.parse("07/07/2026"), sdf.parse("30/06/2026")); // erreur dates
        service.bookRoom(1, 1, sdf.parse("07/07/2026"), sdf.parse("08/07/2026")); // OK
        service.bookRoom(2, 1, sdf.parse("07/07/2026"), sdf.parse("09/07/2026")); // conflit avec user1
        service.bookRoom(2, 3, sdf.parse("07/07/2026"), sdf.parse("08/07/2026")); // OK

        service.setRoom(1, RoomType.MASTER_SUITE, 10000);

        service.printAll();
        service.printAllUsers();
    }
}