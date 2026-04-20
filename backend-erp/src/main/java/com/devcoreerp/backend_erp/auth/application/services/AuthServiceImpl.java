package com.devcoreerp.backend_erp.auth.application.services;

@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {
    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;


    public AuthServiceImpl(
        UserRepository userRepository,
        TokenService tokenService,
        PasswordEncoder passwordEncoder,
        AuthenticationConfiguration authenticationConfiguration
    ) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Override
    public void createUser(final CreateUserDto createUserDto) {
        final User createUser = AuthMapper.fromDto(createUserDto);
        createUser.setPassword(passwordEncoder.encode(createUserDto.password()));
        final User user = userRepository.save(createUser);
        logger.info("[USER] : User successfully created with id {}", user.getId());
    }


    @Override
    public User getUser(final UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("[USER] : User not found with id {}", id);
                return new FincasException(FincasErrorMessage.USER_NOT_FOUND);
            });
    }

    @Override
    public String login(final LoginRequestDTO loginRequest) {
        try {
            final AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
            final Authentication authRequest = AuthMapper.fromDto(loginRequest);
            final Authentication authentication = authenticationManager.authenticate(authRequest);
            return tokenService.generateToken(authentication);

        } catch (Exception e) {
            logger.error("[USER] : Error while trying to login", e);
            throw new ProviderNotFoundException("Error while trying to login");
        }
    }


    @Override
    public boolean validateToken(final String token) {
        return tokenService.validateToken(token);
    }

    @Override
    public String getUserFromToken(final String token) {
        return tokenService.getUserFromToken(token);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        return userRepository.findByEmail(username)
            .orElseThrow(() -> {
                logger.error("[USER] : User not found with email {}", username);
                return new UsernameNotFoundException("User not found");
            });
    }
}