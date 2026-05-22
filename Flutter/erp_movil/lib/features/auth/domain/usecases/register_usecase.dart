import '../entities/user.dart';
import '../repositories/auth_repository.dart';

class RegisterUseCase {
  final AuthRepository repository;

  RegisterUseCase(this.repository);

  Future<UserEntity> call(String name, String surname, String email, String phone, String password) {
    return repository.register(name, surname, email, phone, password);
  }
}
