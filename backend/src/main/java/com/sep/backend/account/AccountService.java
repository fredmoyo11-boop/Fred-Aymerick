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
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;

    public AccountService(CustomerRepository customerRepository, DriverRepository driverRepository) {
        this.customerRepository = customerRepository;
        this.driverRepository = driverRepository;
    }

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


    private AccountDTO getAccountprofild(String username ){
        Optional<CustomerEntity> customerEntity = customerRepository.findByUsername(username);
        if ( customerEntity.isPresent() ) {
            return getCustomerDTO(customerEntity);
        } else {
            Optional<DriverEntity> driverEntity = driverRepository.findByUsername(username);
            if ( driverEntity.isPresent() ) {
                return getDriverDTO(driverEntity);
            }else {
                throw new NotFoundException(ErrorMessages.NOT_FOUND_USER);
            }



        }

    }

    @NotNull
    private static AccountDTO getCustomerDTO(Optional<CustomerEntity> customerEntity) {
        AccountDTO customerDTO = new AccountDTO();
        customerDTO.setEmail(customerEntity.get().getEmail());
        customerDTO.setRatings(customerEntity.get().getRatings());
        customerDTO.setRole(Roles.CUSTOMER);
        customerDTO.setUsername(customerEntity.get().getUsername());
        customerDTO.setFirstName(customerEntity.get().getFirstName());
        customerDTO.setLastName(customerEntity.get().getLastName());
        customerDTO.setProfilePictureUrl( customerEntity.get().getProfilePictureUrl());
        return customerDTO;
    }

    @NotNull
    private static AccountDTO getDriverDTO(Optional<DriverEntity> driverEntity) {
        AccountDTO driverDTO = new AccountDTO();
        driverDTO.setEmail(driverEntity.get().getEmail());
        driverDTO.setRatings(driverEntity.get().getRatings());
        driverDTO.setRole(Roles.DRIVER);
        driverDTO.setUsername(driverEntity.get().getUsername());
        driverDTO.setFirstName(driverEntity.get().getFirstName());
        driverDTO.setLastName(driverEntity.get().getLastName());
        driverDTO.setTotalnumberofrides( driverEntity.get().getTotalnumberofrides() );
        driverDTO.setProfilePictureUrl( driverEntity.get().getProfilePictureUrl());
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
}
