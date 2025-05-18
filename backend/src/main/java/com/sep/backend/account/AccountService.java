package com.sep.backend.account;

import com.sep.backend.CarType;
import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.auth.registration.RegistrationDTO;
import com.sep.backend.auth.registration.RegistrationException;
import com.sep.backend.entity.AccountEntity;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.DriverEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class AccountService {
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
     * Returns an optional containing the customer entity. Optional is empty if the customer with a specified username does not exist.
     *
     * @param username The username of the customer.
     * @return The optional containing the customer entity.
     */
    public Optional<CustomerEntity> findCustomerByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    /**
     * Returns an optional containing the driver entity. Optional is empty if the driver with a specified username doesn't exist.
     *
     * @param username The username of the driver.
     * @return The optional containing the driver entity.
     */
    public Optional<DriverEntity> findDriverByUsername(String username) {
        return driverRepository.findByUsername(username);
    }

    /**
     * Returns an optional containing the role of the specified username. Either CUSTOMER or DRIVER. Optional is empty if the username does not exist.
     *
     * @param username The username.
     * @return The optional containing the role.
     */
    public Optional<String> getRoleByUsername(String username) {
        if (existsCustomerUsername(username)) {
            return Optional.of(Roles.CUSTOMER);
        } else if (existsDriverUsername(username)) {
            return Optional.of(Roles.DRIVER);
        } else {
            return Optional.empty();
        }
    }


    /**
     * Returns the account
     *
     * @param principal
     * @return
     * @throws NotFoundException
     */
    public AccountDTO getCurrentAccount(Principal principal) throws NotFoundException {
        String email = principal.getName();
        String role = getRoleByEmail(email);
        return switch (role) {
            case Roles.CUSTOMER -> {
                var customerEntity = getCustomerByEmail(email);
                yield getCustomerDTO(customerEntity);
            }
            case Roles.DRIVER -> {
                var driverEntity = getDriverByEmail(email);
                yield getDriverDTO(driverEntity);
            }
            default -> throw new NotFoundException(ErrorMessages.NOT_FOUND_USER);
        };
    }


    /**
     * Returns an optional containing the email for the provided username. Returns an empty optional if the username is unknown.
     *
     * @param username The username.
     * @return The optional containing the email. Might be empty if the username is unknown.
     */
    public Optional<String> findEmailByUsername(String username) {
        if (existsCustomerUsername(username)) {
            return customerRepository.findByUsername(username).map(CustomerEntity::getEmail);
        } else if (existsDriverUsername(username)) {
            return driverRepository.findByUsername(username).map(DriverEntity::getEmail);
        } else {
            return Optional.empty();
        }
    }


    /**
     * Verifies account with specified email.
     *
     * @param email The email address of the account.
     * @throws NotFoundException If no account with specified email exists.
     */
    public void verifyAccount(String email) throws NotFoundException {
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
     * Returns whether the user with the specified email is verified or not.
     *
     * @param email The email of the user.
     * @return Whether the user is verified or not.
     * @throws NotFoundException If user with specified username does not exist.
     */
    public boolean isVerified(String email) throws NotFoundException {
        String role = getRoleByEmail(email);
        return switch (role) {
            case Roles.CUSTOMER -> getCustomerByEmail(email).getVerified();
            case Roles.DRIVER -> getDriverByEmail(email).getVerified();
            default -> throw new NotFoundException(ErrorMessages.NOT_FOUND_USER);
        };
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
                String carType = data.getCarType();
                if (!Set.of(CarType.ALL).contains(carType)) {
                    throw new RegistrationException(ErrorMessages.INVALID_CAR_TYPE);
                }
                driverEntity.setCarType(carType);
                driverRepository.save(driverEntity);
                log.info("Saving driver: {} ({})", username, email);
            }
            default -> {
                log.error("Invalid role {} for {} ({})", role, username, email);
                throw new RegistrationException(ErrorMessages.INVALID_ROLE);
            }
        }
    }


    public List<AccountDTO> searchUser(String part) {
        List<AccountDTO> accountDTOS = new ArrayList<>();
        List<DriverEntity> drivers = driverRepository.findByUsernameContainingIgnoreCase(part);
        List<CustomerEntity> customers = customerRepository.findByUsernameContainingIgnoreCase(part);
        if (drivers.isEmpty() && customers.isEmpty()) {
            throw new NotFoundException("Kein Kunde mit Benutzername " + " " + part + " " + "gefunden.");
        } else if (drivers.isEmpty()) {
            accountDTOS.addAll(mapToCustomerDTO(customers));
        } else if (customers.isEmpty()) {
            accountDTOS.addAll(mapToDriverDTO(drivers));
        } else {
            accountDTOS.addAll(mapToAccountDTOs(customers, drivers));
        }
        return accountDTOS;
    }

    private List<AccountDTO> mapToAccountDTOs(List<CustomerEntity> customers, List<DriverEntity> drivers) {
        List<AccountDTO> accountDTOs = new ArrayList<>();
        accountDTOs.addAll(mapToCustomerDTO(customers));
        accountDTOs.addAll(mapToDriverDTO(drivers));
        return accountDTOs;
    }

    private List<AccountDTO> mapToCustomerDTO(List<CustomerEntity> customers) {
        List<AccountDTO> accountDTOS = new ArrayList<>();
        for (CustomerEntity customer : customers) {
            AccountDTO account = getCustomerDTO(customer);
            accountDTOS.add(account);
        }
        return accountDTOS;
    }


    private List<AccountDTO> mapToDriverDTO(List<DriverEntity> drivers) {
        List<AccountDTO> accountDTOS = new ArrayList<>();
        for (DriverEntity driver : drivers) {
            AccountDTO account = getDriverDTO(driver);
            accountDTOS.add(account);
        }
        return accountDTOS;
    }

    @Schema(description = "get the account of  user BASED OF THE username .Die Methode ist erstmal für Unterstützung des frontend Features : klickbares profil gedacht")
    public AccountDTO getAccountProfile(String username) {
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
        customerDTO.setBirthday(customerEntity.getBirthday().toString());
        customerDTO.setUsername(customerEntity.getUsername());
        customerDTO.setFirstName(customerEntity.getFirstName());
        customerDTO.setLastName(customerEntity.getLastName());
        customerDTO.setTotalNumberOfRides(5);
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
        driverDTO.setBirthday(driverEntity.getBirthday().toString());
        driverDTO.setCarType(driverEntity.getCarType());
        driverDTO.setTotalNumberOfRides(5);
        driverDTO.setProfilePictureUrl(driverEntity.getProfilePictureUrl());
        return driverDTO;
    }


    /**
     * Returns if an account with the specified username exists.
     *
     * @param username The username.
     * @return Whether an account with the username exists or not.
     */
    public boolean existsUsername(String username) {
        return existsCustomerUsername(username) || existsDriverUsername(username);
    }

    /**
     * Returns if a customer with the specified username exists.
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
            account.setTotalNumberOfRides(0);
            account.setVerified(false);
            account.setProfilePictureUrl(profilePictureUrl);
            return account;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create account entity of type " + clazz.getName(), e);
        }
    }

    @Schema(description = "Updates the account of the authenticated user")
    public void saveAccountChanges(String email, UpdateAccountDTO updateAccountDTO, MultipartFile file) {
        if (isOwner(email)) {
            if (existsCustomerEmail(email)) {
                updateCustomer(email, updateAccountDTO, file);
            } else if (existsDriverEmail(email)) {
                updateDriver(email, updateAccountDTO, file);
            } else {
                throw new NotFoundException(ErrorMessages.NOT_FOUND_USER);
            }
        } else {
            throw new RuntimeException("You are not allowed to update this account.");
        }
    }

    public void updateCustomer(String email, UpdateAccountDTO updateAccountDTO, MultipartFile file) {

        try {
            CustomerEntity customerEntity = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_USER));


            if (updateAccountDTO.getUsername() != null && !customerEntity.getUsername().equals(updateAccountDTO.getUsername())) {
                if (existsCustomerUsername(updateAccountDTO.getUsername()) || existsDriverUsername(updateAccountDTO.getUsername())) {
                    throw new IllegalArgumentException("Username already exists");
                } else {
                    customerEntity.setUsername(updateAccountDTO.getUsername());
                }
            }
            if (updateAccountDTO.getFirstName() != null) {
                customerEntity.setFirstName(updateAccountDTO.getFirstName());
            }
            if (updateAccountDTO.getLastName() != null) {
                customerEntity.setLastName(updateAccountDTO.getLastName());
            }
            if (updateAccountDTO.getBirthday() != null) {
                customerEntity.setBirthday(updateAccountDTO.getBirthday());
            }
            if (file != null) {
                String profilePictureUrl = profilePictureStorageService.save(file, customerEntity.getUsername());
                customerEntity.setProfilePictureUrl(profilePictureUrl);
            }
            // Kunde speichern
            customerRepository.save(customerEntity);
            log.info("Updated customer {} with username {}", customerEntity.getUsername(), customerEntity.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update account entity of type " + CustomerEntity.class.getName(), e);
        }


    }

    public void updateDriver(String email, UpdateAccountDTO updateAccountDTO, MultipartFile file) {
        try {
            DriverEntity driverEntity = driverRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_USER));
            if (updateAccountDTO.getUsername() != null && !updateAccountDTO.getUsername().equals(driverEntity.getUsername())) {
                if (existsDriverUsername(updateAccountDTO.getUsername()) || existsCustomerUsername(updateAccountDTO.getUsername())) {
                    throw new IllegalArgumentException("Username already exists,update failed!");
                } else {
                    driverEntity.setUsername(updateAccountDTO.getUsername());
                }
            }
            // Felder nur aktualisieren, wenn sie nicht null sind
            if (updateAccountDTO.getFirstName() != null) {
                driverEntity.setFirstName(updateAccountDTO.getFirstName());
            }
            if (updateAccountDTO.getLastName() != null) {
                driverEntity.setLastName(updateAccountDTO.getLastName());
            }
            if (updateAccountDTO.getBirthday() != null) {
                driverEntity.setBirthday(updateAccountDTO.getBirthday());
            }
            if (updateAccountDTO.getCarType() != null) {
                driverEntity.setCarType(updateAccountDTO.getCarType());
            }
            if (file != null) {
                String profilePictureUrl = profilePictureStorageService.save(file, driverEntity.getUsername());
                driverEntity.setProfilePictureUrl(profilePictureUrl);
            }
            driverRepository.save(driverEntity);
            log.info("Updated driver {} with email {}", driverEntity.getUsername(), driverEntity.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update account entity of type " + DriverEntity.class.getName(), e);
        }
    }

    public boolean isOwner(String email) {
        return SecurityContextHolder.getContext().getAuthentication().getName().equals(email);
    }

}