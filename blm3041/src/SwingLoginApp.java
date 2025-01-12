
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// User class to store logged-in user details
class User {
    private int id;
    private String username;
    private String fname;
    private String lname;
    private String address;

    public User(int id, String username, String fname, String lname, String address) {
        this.id = id;
        this.username = username;
        this.fname = fname;
        this.lname = lname;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }
}

public class SwingLoginApp {
    // Database connection details
    static final String DB_URL = "jdbc:postgresql://localhost/blm3041";
    static final String DB_USER = "postgres";
    static final String DB_PASSWORD = "kali";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}

// Login Frame
class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginFrame() {
        setTitle("Login System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());

        messageLabel = new JLabel("", SwingConstants.CENTER);

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Empty placeholder
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
            User user = validateCredentials(conn, username, password);

            if (user != null) {
                messageLabel.setText("Login successful!");
                new HomepageFrame(user).setVisible(true);;
                dispose();
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            messageLabel.setText("Database error.");
        }
    }

    private User validateCredentials(Connection conn, String username, String password) throws SQLException {
        String query = "SELECT * FROM person WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("fname"),
                        rs.getString("lname"),
                        rs.getString("address")
                );
            }
        }
        return null;
    }
}

// Homepage Frame
class HomepageFrame extends JFrame {
    private User user;

        public HomepageFrame(User user) {
            this.user = user;

            setTitle("Personal Homepage");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 400);
            setLocationRelativeTo(null);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));

            JButton accountInfoButton = new JButton("View Account Information");
            JButton viewPetsButton = new JButton("View My Pets");
            JButton vetReservationButton = new JButton("Make a Veterinary Appointment");
            JButton petStoreButton = new JButton("Go to Pet Store");
            JButton logoutButton = new JButton("Logout");

            accountInfoButton.addActionListener(e -> viewAccountInfo());
            viewPetsButton.addActionListener(e -> {
                dispose(); // Close the HomepageFrame
                new PetsFrame(user).setVisible(true);
            });
            vetReservationButton.addActionListener(e -> {
                dispose(); // Close the HomepageFrame
                new AppointmentFrame(user).setVisible(true);
            });
            petStoreButton.addActionListener(e -> {
                dispose(); // Close the HomepageFrame
                new PetStoreFrame(user).setVisible(true);
            });
            logoutButton.addActionListener(e -> logout());

            buttonPanel.add(accountInfoButton);
            buttonPanel.add(viewPetsButton);
            buttonPanel.add(vetReservationButton);
            buttonPanel.add(petStoreButton);
            buttonPanel.add(logoutButton);

            add(buttonPanel, BorderLayout.CENTER);


        }


        private void logout() {
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                dispose(); // Close the HomepageFrame
                new LoginFrame().setVisible(true); // Redirect to login screen
            }
        }
    


    private void viewAccountInfo() {
        JOptionPane.showMessageDialog(this,
                "Full Name: " + user.getFname() + " " + user.getLname() +
                        "\nAddress: " + user.getAddress() +
                        "\nUsername: " + user.getUsername(),
                "Account Information", JOptionPane.INFORMATION_MESSAGE);
    }


}


// CartItem class to store item details and quantity
class CartItem {
    private String itemName;
    private int quantity;
    private double price;

    public CartItem(String itemName, int quantity, double price) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalPrice() {
        return quantity * price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

// PetStoreFrame for managing the pet store
class PetStoreFrame extends JFrame {
    private User user;
    private Map<String, Double> items;
    private ArrayList<CartItem> cart;
    private Connection connection;
    private JFrame homepageFrame;

    public PetStoreFrame(User user) {
        this.user = user;
        this.items = new HashMap<>();
        this.cart = new ArrayList<>();

        setTitle("Pet Store");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Load items from database
        try {
            connection = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD);
            loadItemsFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        items.forEach((item, price) -> listModel.addElement(item + " - $" + price));

        JList<String> itemList = new JList<>(listModel);
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTextField quantityField = new JTextField();
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> addToCart(itemList, quantityField));

        JButton viewCartButton = new JButton("View Cart");
        viewCartButton.addActionListener(e -> {
            dispose();
            new CartFrame(user, cart, this).setVisible(true);
        });

        JButton backButton = new JButton("Go Back");
        backButton.addActionListener(e -> {
        	     new HomepageFrame(user).setVisible(true);;
        	     dispose();
        	 }
        );

        JPanel controlPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        controlPanel.add(new JLabel("Select Item:"));
        controlPanel.add(new JScrollPane(itemList));
        controlPanel.add(new JLabel("Quantity:"));
        controlPanel.add(quantityField);
        controlPanel.add(addToCartButton);
        controlPanel.add(viewCartButton);
        controlPanel.add(backButton);

        mainPanel.add(controlPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void loadItemsFromDatabase() {
        String query = "SELECT name, price FROM product";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                items.put(name, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToCart(JList<String> itemList, JTextField quantityField) {
        String selectedValue = itemList.getSelectedValue();
        if (selectedValue == null) {
            JOptionPane.showMessageDialog(this, "Please select an item.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String itemName = selectedValue.split(" - ")[0];
        double price = items.get(itemName);

        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) {
                throw new NumberFormatException();
            }

            boolean itemExists = false;
            for (CartItem item : cart) {
                if (item.getItemName().equals(itemName)) {
                    item.setQuantity(item.getQuantity() + quantity);
                    itemExists = true;
                    break;
                }
            }

            if (!itemExists) {
                cart.add(new CartItem(itemName, quantity, price));
            }

            JOptionPane.showMessageDialog(this, "Item added to cart.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// CartFrame to view and manage cart items
class CartFrame extends JFrame {
    private ArrayList<CartItem> cart;
    private JFrame petStoreFrame;

    public CartFrame(User user, ArrayList<CartItem> cart, JFrame petStoreFrame) {
        this.cart = cart;
        this.petStoreFrame = petStoreFrame;

        setTitle("Cart");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        DefaultListModel<String> cartModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartModel);
        updateCartModel(cartModel);

        JButton removeButton = new JButton("Remove Selected Item");
        removeButton.addActionListener(e -> removeSelectedItem(cartList, cartModel));

        JButton buyButton = new JButton("Buy Items");
        buyButton.addActionListener(e -> buyItems());

        JButton backButton = new JButton("Back to Store");
        backButton.addActionListener(e -> {
            dispose();
            petStoreFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.add(removeButton);
        buttonPanel.add(buyButton);
        buttonPanel.add(backButton);

        add(new JScrollPane(cartList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateCartModel(DefaultListModel<String> cartModel) {
        cartModel.clear();
        for (CartItem item : cart) {
            cartModel.addElement(item.getItemName() + " - Quantity: " + item.getQuantity() + " - Total: $" + item.getTotalPrice());
        }
    }

    private void removeSelectedItem(JList<String> cartList, DefaultListModel<String> cartModel) {
        int selectedIndex = cartList.getSelectedIndex();
        if (selectedIndex != -1) {
            cart.remove(selectedIndex);
            updateCartModel(cartModel);
            JOptionPane.showMessageDialog(this, "Item removed from cart.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buyItems() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        cart.clear();
        JOptionPane.showMessageDialog(this, "Thank you for your purchase!", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        petStoreFrame.setVisible(true);
    }
}




//Updated PetsFrame with "Go Back" button and ReservationsFrame
class PetsFrame extends JFrame {
 private User user;

 public PetsFrame(User user) {
     this.user = user;

     setTitle("My Pets");
     setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
     setSize(500, 400);
     setLocationRelativeTo(null);

     JPanel buttonPanel = new JPanel();
     buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));

     JButton viewPetsButton = new JButton("View Pets");
     JButton addPetButton = new JButton("Add New Pet");
     JButton viewPetDetailsButton = new JButton("View Pet Details");
     JButton goBackButton = new JButton("Go Back");

     viewPetsButton.addActionListener(e -> viewPets());
     addPetButton.addActionListener(e -> addPet());
     viewPetDetailsButton.addActionListener(e -> viewPetDetails());
     goBackButton.addActionListener(e -> goBack());

     buttonPanel.add(viewPetsButton);
     buttonPanel.add(addPetButton);
     buttonPanel.add(viewPetDetailsButton);
     buttonPanel.add(goBackButton);

     add(buttonPanel, BorderLayout.CENTER);

 }

 private void viewPets() {
     // View pets implementation
     try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
         String query = "SELECT * FROM pet WHERE owner_id = ?";
         try (PreparedStatement stmt = conn.prepareStatement(query)) {
             stmt.setInt(1, user.getId());
             ResultSet rs = stmt.executeQuery();

             StringBuilder petsInfo = new StringBuilder("Your Pets:\n");
             while (rs.next()) {
                 petsInfo.append("-- ID: ").append(rs.getInt("id")).append(", ");
                 petsInfo.append("Name: ").append(rs.getString("name")).append(", ");
                 petsInfo.append("Species: ").append(rs.getString("species")).append(", ");
                 petsInfo.append("Breed: ").append(rs.getString("breed")).append(", ");
                 petsInfo.append("Age: ").append(rs.getInt("age")).append(", ");
                 petsInfo.append("Sex: ").append(rs.getString("sex")).append("\n");
             }

             if (petsInfo.length() == 10) {
                 petsInfo.append("You have no pets.");
             }

             JOptionPane.showMessageDialog(this, petsInfo.toString(), "My Pets", JOptionPane.INFORMATION_MESSAGE);
         }
     } catch (SQLException ex) {
         ex.printStackTrace();
         JOptionPane.showMessageDialog(this, "Error fetching pets.", "Error", JOptionPane.ERROR_MESSAGE);
     }
 }

 private void addPet() {
     // Add pet implementation
     int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter pet id:"));
     String name = JOptionPane.showInputDialog(this, "Enter pet name:");
     String species = JOptionPane.showInputDialog(this, "Enter pet species:");
     String breed = JOptionPane.showInputDialog(this, "Enter pet breed:");
     int age = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter pet age:"));
     String sex = JOptionPane.showInputDialog(this, "Enter pet sex (M or F):");

     try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
         String query = "INSERT INTO pet (id, name, owner_id, species, breed, age, sex) VALUES (?, ?, ?, ?, ?, ?, ?)";
         try (PreparedStatement stmt = conn.prepareStatement(query)) {
             stmt.setInt(1, id);
             stmt.setString(2, name);
             stmt.setInt(3, user.getId());
             stmt.setString(4, species);
             stmt.setString(5, breed);
             stmt.setInt(6, age);
             stmt.setString(7, sex);
             stmt.executeUpdate();

             JOptionPane.showMessageDialog(this, "Pet added successfully!", "Success",
                     JOptionPane.INFORMATION_MESSAGE);
         }
     } catch (SQLException ex) {
         ex.printStackTrace();
         JOptionPane.showMessageDialog(this, "Error adding pet.", "Error", JOptionPane.ERROR_MESSAGE);
     }
 }

 private void viewPetDetails() {
	    // Create a new JFrame for displaying pet vaccination and check-up details
	    JFrame petDetailsFrame = new JFrame("Pet Details");
	    petDetailsFrame.setSize(500, 400);
	    petDetailsFrame.setLocationRelativeTo(this);
	    petDetailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	    JPanel panel = new JPanel();
	    panel.setLayout(new BorderLayout());

	    JComboBox<String> petComboBox = new JComboBox<>();
	    JButton showDetailsButton = new JButton("Show Details");

	    // Populate the JComboBox with the user's pets
	    try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
	        String query = "SELECT id, name FROM pet WHERE owner_id = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setInt(1, user.getId()); // Get only pets belonging to the logged-in user
	            ResultSet rs = stmt.executeQuery();
	            while (rs.next()) {
	                petComboBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
	            }
	        }
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Error fetching pets.", "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    // If no pets are found, show a message and close the frame
	    if (petComboBox.getItemCount() == 0) {
	        JOptionPane.showMessageDialog(this, "No pets found for this user.", "No Pets", JOptionPane.INFORMATION_MESSAGE);
	        return;
	    }

	    panel.add(new JLabel("Select Pet:"), BorderLayout.NORTH);
	    panel.add(petComboBox, BorderLayout.CENTER);
	    panel.add(showDetailsButton, BorderLayout.SOUTH);

	    petDetailsFrame.add(panel);
	    petDetailsFrame.setVisible(true);


	    // Action listener for the "Show Details" button
	    showDetailsButton.addActionListener(e -> {
	        String selectedPet = (String) petComboBox.getSelectedItem();
	        if (selectedPet != null) {
	            int petId = Integer.parseInt(selectedPet.split(" - ")[0]);

	            // Fetch vaccination and check-up details
	            JFrame detailsFrame = new JFrame("Vaccination and Check-Up Details");
	            detailsFrame.setSize(500, 400);
	            detailsFrame.setLocationRelativeTo(petDetailsFrame);
	            detailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	            JTextArea detailsArea = new JTextArea();
	            detailsArea.setEditable(false);

	            try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
	                String query = "SELECT vaccination_name, date FROM vaccinations WHERE pet_id = ?";
	                try (PreparedStatement stmt = conn.prepareStatement(query)) {
	                    stmt.setInt(1, petId);
	                    ResultSet rs = stmt.executeQuery();

	                    StringBuilder details = new StringBuilder("Vaccination and Check-Up Details:\n");
	                    while (rs.next()) {
	                        details.append("Vaccination: ").append(rs.getString("vaccination_name")).append(", ");
	                        details.append("Date: ").append(rs.getDate("date")).append("\n");
	                    }

	                    if (details.length() == 33) { // Length of "Vaccination and Check-Up Details:\n"
	                        details.append("No records found.");
	                    }

	                    detailsArea.setText(details.toString());
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	                JOptionPane.showMessageDialog(petDetailsFrame, "Error fetching pet details.", "Error", JOptionPane.ERROR_MESSAGE);
	                return;
	            }

	            detailsFrame.add(new JScrollPane(detailsArea));
	            detailsFrame.setVisible(true);
	        }
	    });
	}


 private void goBack() {
     // Go back to homepage
     new HomepageFrame(user).setVisible(true);;
     dispose();
 }
}

//ReservationsFrame Class


// AppointmentFrame class to handle pet appointments
class AppointmentFrame extends JFrame {
    private User user;
    private JComboBox<String> petComboBox;
    private JComboBox<String> appointmentTypeComboBox;
    private JSpinner dateSpinner;
    private DefaultListModel<String> appointmentListModel;

    public AppointmentFrame(User user) {
        this.user = user;

        setTitle("Make an Appointment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        // Fetch pets and appointment types from the database
        petComboBox = new JComboBox<>(fetchPetsFromDatabase());
        appointmentTypeComboBox = new JComboBox<>(fetchAppointmentTypesFromDatabase());

        JLabel petLabel = new JLabel("Select Pet:");
        JLabel appointmentTypeLabel = new JLabel("Select Appointment Type:");
        JLabel dateLabel = new JLabel("Select Date:");

        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        JButton makeAppointmentButton = new JButton("Make Appointment");
        makeAppointmentButton.addActionListener(e -> makeAppointment());

        JButton viewAppointmentsButton = new JButton("View Appointments");
        viewAppointmentsButton.addActionListener(e -> viewAppointments());

        JButton backButton = new JButton("Back to Homepage");
        backButton.addActionListener(e -> {
            dispose();
            new HomepageFrame(user).setVisible(true);
        });

        formPanel.add(petLabel);
        formPanel.add(petComboBox);
        formPanel.add(appointmentTypeLabel);
        formPanel.add(appointmentTypeComboBox);
        formPanel.add(dateLabel);
        formPanel.add(dateSpinner);
        formPanel.add(makeAppointmentButton);
        formPanel.add(viewAppointmentsButton);

        add(formPanel, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }

    private String[] fetchPetsFromDatabase() {
        ArrayList<String> pets = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
            String query = "SELECT id FROM pet WHERE owner_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, user.getId());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    pets.add(rs.getString("id"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch pets from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return pets.toArray(new String[0]);
    }

    private String[] fetchAppointmentTypesFromDatabase() {
        ArrayList<String> appointmentTypes = new ArrayList<>();
        appointmentTypes.add("Vaccination");
        appointmentTypes.add("Check-up");

        return appointmentTypes.toArray(new String[0]);
    }

    private void makeAppointment() {
        String selectedPet = (String) petComboBox.getSelectedItem();
        String selectedAppointmentType = (String) appointmentTypeComboBox.getSelectedItem();
        java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
            // Query to insert the appointment with sequence-generated appointment_id
            String query = "INSERT INTO appointment (appointment_id, owner_id, pet_id, type, op_date) VALUES (nextval('appointment_seq'), ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, user.getId()); // owner_id
                stmt.setInt(2, Integer.parseInt(selectedPet)); // pet_id

                stmt.setString(3, selectedAppointmentType); // type
                stmt.setDate(4, sqlDate); // op_date
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Appointment made successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to make appointment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void viewAppointments() {
        JFrame viewFrame = new JFrame("Your Appointments");
        viewFrame.setSize(500, 300);
        viewFrame.setLocationRelativeTo(null);

        appointmentListModel = new DefaultListModel<>();
        JList<String> appointmentList = new JList<>(appointmentListModel);
        fetchAppointmentsFromDatabase();

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> viewFrame.dispose());

        viewFrame.add(new JScrollPane(appointmentList), BorderLayout.CENTER);
        viewFrame.add(closeButton, BorderLayout.SOUTH);
        viewFrame.setVisible(true);
    }

    private void fetchAppointmentsFromDatabase() {
        appointmentListModel.clear();
        try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
            String query = "SELECT pet_id, type, op_date FROM appointment WHERE owner_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, user.getId());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                	int petId = rs.getInt("pet_id");
                    String appointmentType = rs.getString("type");
                    String appointmentDate = rs.getDate("op_date").toString();
                    appointmentListModel.addElement(" --Id: " + petId +  " --Type: " + appointmentType + " --Date: " + appointmentDate);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch appointments from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
