package com.sep.backend.account;

import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.auth.registration.RegistrationDTO;
import com.sep.backend.auth.registration.RegistrationException;
import com.sep.backend.entity.AccountEntity;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.DriverEntity;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class AccountService {
    private final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final ProfilePictureStorageService profilePictureStorageService;


    /**
     * Returns the customer with the specified email.
     *
     * @param email The email address.
     * @return The customer entity.
     * @throws NotFoundException If customer with specified email does not exist.
     */
    public CustomerEntity getCustomerByEmail(String email) throws NotFoundException {
        return customerRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_CUSTOMER));
    }

    /**
     * Returns the driver with the specified email.
     *
     * @param email The email address.
     * @return The driver entity.
     * @throws NotFoundException If driver with specified email does not exist.
     */
    public DriverEntity getDriverByEmail(String email) throws NotFoundException {
        return driverRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_DRIVER));
    }

    /**
     * Returns the role (CUSTOMER or DRIVER) of an email if account exists.
     *
     * @param email The email address.
     * @return The role of the account with the specified email address.
     * @throws NotFoundException If neither CUSTOMER nor DRIVER with email address exists.
     */
    public String getRoleByEmail(String email) throws NotFoundException {
        if (existsCustomerEmail(email)) {
            return Roles.CUSTOMER;
        } else if (existsDriverEmail(email)) {
            return Roles.DRIVER;
        } else {
            throw new NotFoundException(ErrorMessages.NOT_FOUND_USER);
        }
    }

    /**
     * Verifies account with specified email.
     *
     * @param email The email address of the account.
     * @throws NotFoundException If no account with specified email exists.
     */
    public void verifyAccount(String email) throws RegistrationException {
        String role = getRoleByEmail(email);
        switch (role) {
            case Roles.CUSTOMER -> {
                var customerEntity = getCustomerByEmail(email);
                customerEntity.setVerified(true);
                customerRepository.save(customerEntity);
                log.info("Verified customer {}", email);
            }
            case Roles.DRIVER -> {
                var driverEntity = getDriverByEmail(email);
                driverEntity.setVerified(true);
                driverRepository.save(driverEntity);
                log.info("Verified driver {}", email);
            }
            default -> throw new NotFoundException(ErrorMessages.NOT_FOUND_USER);
        }
    }

    /**
     * Creates a new account (CUSTOMER or DRIVER).
     *
     * @param data              The data of the account.
     * @param profilePictureUrl The (optional) url to the profile picture.
     * @throws RegistrationException If invalid role is provided.
     */
    public void createAccount(RegistrationDTO data, String profilePictureUrl) throws RegistrationException {
        String role = data.getRole();
        String email = data.getEmail();
        String username = data.getUsername();
        switch (role) {
            case Roles.CUSTOMER -> {
                log.debug("Saving customer: {} ({})", username, email);
                var customerEntity = createAccountEntity(data, profilePictureUrl, CustomerEntity.class);
                customerRepository.save(customerEntity);
                log.info("Saving customer: {} ({})", username, email);
            }
            case Roles.DRIVER -> {
                log.debug("Saving driver: {} ({})", username, email);
                var driverEntity = createAccountEntity(data, profilePictureUrl, DriverEntity.class);
                driverRepository.save(driverEntity);
                log.info("Saving driver: {} ({})", username, email);
            }
            default -> {
                log.error("Invalid role {} for {} ({})", role, username, email);
                throw new RegistrationException(ErrorMessages.INVALID_ROLE);
            }
        }
    }

    public List<AccountDTO> userSearch(String part) {
        List<AccountDTO> accounts = new ArrayList<>();
        List<DriverEntity> drivers = driverRepository.findByUsernameContainingIgnoreCase(part);
        List<CustomerEntity> customers = customerRepository.findByUsernameContainingIgnoreCase(part);
        if (drivers.isEmpty() && customers.isEmpty()) {
            throw new NotFoundException("Kein Kunde mit Benutzername " + " " + part + " " + "gefunden.");
        } else if (drivers.isEmpty()) {
            accounts.addAll(mapToCustomerDTO(customers));
        }else if (customers.isEmpty()) {
            accounts.addAll(mapToDriverDTO(drivers));
        }else{
            accounts.addAll(mapToCustomerDTO(customers));
            accounts.addAll(mapToDriverDTO(drivers));
        }
        return accounts;
    }

    private List<AccountDTO> mapToCustomerDTO(List<CustomerEntity> customers) {
        List<AccountDTO> accounts = new ArrayList<>();
        for (CustomerEntity customer : customers) {
            AccountDTO account = getCustomerDTO(customer);
            accounts.add(account);
        }
        return accounts;
    }



    private List<AccountDTO> mapToDriverDTO(List<DriverEntity> drivers) {
        List<AccountDTO> accounts = new ArrayList<>();
        for (DriverEntity driver : drivers) {
            AccountDTO account = getDriverDTO(driver);
            accounts.add(account);
        }
        return accounts;
    }


    public AccountDTO getAccountprofild(String username) {
        Optional<CustomerEntity> customerEntity = customerRepository.findByUsername(username);
        if (customerEntity.isPresent()) {
            return getCustomerDTO(customerEntity.get());
        } else {
            Optional<DriverEntity> driverEntity = driverRepository.findByUsername(username);
            if (driverEntity.isPresent()) {
                return getDriverDTO(driverEntity.get());
            } else {
                throw new NotFoundException(ErrorMessages.NOT_FOUND_USER);
            }
        }
    }

    public static AccountDTO getCustomerDTO(CustomerEntity customerEntity) {
        AccountDTO customerDTO = new AccountDTO();
        customerDTO.setEmail(customerEntity.getEmail());
        customerDTO.setRatings(customerEntity.getRating());
        customerDTO.setRole(Roles.CUSTOMER);
        customerDTO.setUsername(customerEntity.getUsername());
        customerDTO.setFirstName(customerEntity.getFirstName());
        customerDTO.setLastName(customerEntity.getLastName());
        customerDTO.setProfilePictureUrl(customerEntity.getProfilePictureUrl());
        return customerDTO;
    }


    public static AccountDTO getDriverDTO(DriverEntity driverEntity) {
        AccountDTO driverDTO = new AccountDTO();
        driverDTO.setEmail(driverEntity.getEmail());
        driverDTO.setRatings(driverEntity.getRating());
        driverDTO.setRole(Roles.DRIVER);
        driverDTO.setUsername(driverEntity.getUsername());
        driverDTO.setFirstName(driverEntity.getFirstName());
        driverDTO.setLastName(driverEntity.getLastName());
        driverDTO.setCarType(driverEntity.getCarType());
        driverDTO.setTotalnumberofrides(driverEntity.getTotalnumberofrides());
        driverDTO.setProfilePictureUrl(driverEntity.getProfilePictureUrl());
        return driverDTO;
    }


    /**
     * Returns if an account with specified username exists.
     *
     * @param username The username.
     * @return Whether account with username exists or not.
     */
    public boolean existsUsername(String username) {
        return existsCustomerUsername(username) || existsDriverUsername(username);
    }

    /**
     * Returns if a customer with specified username exists.
     *
     * @param username The username.
     * @return Whether customer with username exists or not.
     */
    private boolean existsCustomerUsername(String username) {
        return customerRepository.existsByUsername(username);
    }

    /**
     * Returns if a driver with specified username exists.
     *
     * @param username The username.
     * @return Whether driver with username exists or not.
     */
    private boolean existsDriverUsername(String username) {
        return driverRepository.existsByUsername(username);
    }

    /**
     * Returns if an account with specified email address exists.
     *
     * @param email The email address.
     * @return Whether account with email address exists or not.
     */
    public boolean existsEmail(String email) {

        return existsCustomerEmail(email) || existsDriverEmail(email);

    }


    /**
     * Returns if a customer with specified email address exists.
     *
     * @param email The email address.
     * @return Whether customer with email address exists or not.
     */
    private boolean existsCustomerEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    /**
     * Returns if a driver with specified email address exists.
     *
     * @param email The email address.
     * @return Whether driver with email address exists or not.
     */
    private boolean existsDriverEmail(String email) {
        return driverRepository.existsByEmail(email);
    }


    /**
     * Creates a new entity that derives from AccountEntity.
     *
     * @param data              The account data.
     * @param profilePictureUrl The public profile picture url.
     * @param clazz             The class of the derived entity.
     * @param <T>               The class of the entity.
     * @return The created entity.
     */
    private <T extends AccountEntity> T createAccountEntity(@Valid RegistrationDTO data, String profilePictureUrl, Class<T> clazz) {
        try {
            T account = clazz.getDeclaredConstructor().newInstance();
            account.setEmail(data.getEmail());
            account.setPassword(data.getPassword());
            account.setUsername(data.getUsername());
            account.setFirstName(data.getFirstName());
            account.setLastName(data.getLastName());
            account.setBirthday(data.getBirthday());
            account.setVerified(false);
            account.setProfilePictureUrl(profilePictureUrl);
            return account;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create account entity of type " + clazz.getName(), e);
        }
    }

    public void updateAccount(String username, UpdateAccountDTO updateAccountDTO, MultipartFile file) {
        // Authentifizierten Benutzer abrufen (Email)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmail = authentication.getName();
        // Username des authentifizierten Benutzers anhand der Email ermitteln
        String authenticatedUsername = resolveUsernameByEmail(authenticatedEmail);
        // Prüfen, ob der angegebene Username mit dem authentifizierten Username übereinstimmt
        if (authenticatedUsername.equals(username)) {
            // Profilbild setzen
            updateAccountDTO.setProfilePicture(file);
            // Prüfen, ob der Benutzer ein Kunde oder Fahrer ist, und entsprechend updaten
            if (existsCustomerUsername(username)) {
                updateCustomer(username, updateAccountDTO);
            } else if (existsDriverUsername(username)) {
                updateDriver(username, updateAccountDTO);
            } else {
                throw new NotFoundException(ErrorMessages.NOT_FOUND_USER);
            }
        } else {
            throw new IllegalArgumentException("The provided username does not match the authenticated user.");
        }
    }
    private String resolveUsernameByEmail(String email) {
        // Prüfen, ob der Benutzer ein Kunde ist, und Username zurückgeben
        if (customerRepository.existsByEmail(email)) {
            return customerRepository.findByEmail(email).get().getUsername();
        }
        // Prüfen, ob der Benutzer ein Fahrer ist, und Username zurückgeben
        if (driverRepository.existsByEmail(email)) {
            return driverRepository.findByEmail(email).get().getUsername();
        }
        // Benutzer nicht gefunden
        throw new NotFoundException("No user found with the provided email: " + email);
    }





    public void updateCustomer(String username, UpdateAccountDTO updateAccountDTO)  {

        if (!existsCustomerUsername(updateAccountDTO.getUsername()) ) {
            CustomerEntity customerEntity = customerRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_USER));
            // Felder nur aktualisieren, wenn sie nicht null sind
            if (updateAccountDTO.getFirstName() != null) {
                customerEntity.setFirstName(updateAccountDTO.getFirstName());
            }
            if (updateAccountDTO.getLastName() != null) {
                customerEntity.setLastName(updateAccountDTO.getLastName());
            }
            if (updateAccountDTO.getUsername() != null) {
                customerEntity.setUsername(updateAccountDTO.getUsername());
            }
            if (updateAccountDTO.getBirthday() != null) {
                customerEntity.setBirthday(updateAccountDTO.getBirthday());
            }
            if (updateAccountDTO.getProfilePicture() != null) {
                String profilePictureUrl = profilePictureStorageService.save(updateAccountDTO.getProfilePicture(), username);
                customerEntity.setProfilePictureUrl(profilePictureUrl);
            }
            // Kunde speichern
            customerRepository.save(customerEntity);
            log.info("Updated customer {} with username {}", customerEntity.getUsername(), customerEntity.getEmail());
        } else {
            throw new IllegalArgumentException("Username already exists");
        }
    }

    private void updateDriver(String username, UpdateAccountDTO updateAccountDTO) {
        if (!existsDriverUsername(updateAccountDTO.getUsername()) ) {
            DriverEntity driverEntity = driverRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_USER));
            // Felder nur aktualisieren, wenn sie nicht null sind
            if (updateAccountDTO.getFirstName() != null) {
                driverEntity.setFirstName(updateAccountDTO.getFirstName());
            }
            if (updateAccountDTO.getLastName() != null) {
                driverEntity.setLastName(updateAccountDTO.getLastName());
            }
            if (updateAccountDTO.getUsername() != null) {
                driverEntity.setUsername(updateAccountDTO.getUsername());
            }
            if (updateAccountDTO.getBirthday() != null) {
                driverEntity.setBirthday(updateAccountDTO.getBirthday());
            }
            if (updateAccountDTO.getCarType() != null) {
                driverEntity.setCarType(updateAccountDTO.getCarType());
            }
            if (updateAccountDTO.getProfilePicture() != null) {
                String profilePictureUrl = profilePictureStorageService.save(updateAccountDTO.getProfilePicture(), username);
                driverEntity.setProfilePictureUrl(profilePictureUrl);
            }
            // Fahrer speichern
            driverRepository.save(driverEntity);
            log.info("Updated driver {} with username {}", driverEntity.getUsername(), driverEntity.getEmail());
        }else{
            throw new IllegalArgumentException("Username already exists");
        }
    }

}