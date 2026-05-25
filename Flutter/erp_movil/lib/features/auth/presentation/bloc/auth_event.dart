import 'package:equatable/equatable.dart';

abstract class AuthEvent extends Equatable {
  const AuthEvent();

  @override
  List<Object> get props => [];
}

class LoginEvent extends AuthEvent {
  final String email;
  final String password;

  const LoginEvent(this.email, this.password);

  @override
  List<Object> get props => [email, password];
}

class RegisterEvent extends AuthEvent {
  final String name;
  final String surname;
  final String email;
  final String phone;
  final String password;

  const RegisterEvent(this.name, this.surname, this.email, this.phone, this.password);

  @override
  List<Object> get props => [name, surname, email, phone, password];
}
