package com.example.evenz;
/**
 * The {@code Admin} class represents an administrative user, extending the {@code User} class.
 * It includes additional functionalities and attributes specific to administrative roles.
 *
 * <p>Instances of this class can be created with the provided constructor, which initializes
 * the common user details using the {@code User} constructor.
 *
 * <p>This class serves as a foundation for implementing administrative functionalities within
 * the application.
 *
 * @author maheeee1207
 * @version 1.0
 * @see User
 */
public class Admin extends User
{

    public Admin(String name, String profilePicID, String phone, String email) {
        super(name, profilePicID, phone, email);
    }
}