import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../../core/errors/failures.dart';
import '../../domain/usecases/login_usecase.dart';
import '../../domain/usecases/register_usecase.dart';
import 'auth_event.dart';
import 'auth_state.dart';

class AuthBloc extends Bloc<AuthEvent, AuthState> {
  final LoginUseCase loginUseCase;
  final RegisterUseCase registerUseCase;

  AuthBloc({
    required this.loginUseCase,
    required this.registerUseCase,
  }) : super(AuthInitial()) {
    on<LoginEvent>((event, emit) async {
      emit(AuthLoading());
      try {
        final user = await loginUseCase(event.email, event.password);
        emit(AuthSuccess(user));
      } catch (e) {
        if (e is Failure) {
          emit(AuthError(e.message));
        } else {
          emit(AuthError(e.toString()));
        }
      }
    });

    on<RegisterEvent>((event, emit) async {
      emit(AuthLoading());
      try {
        final user = await registerUseCase(
          event.name,
          event.surname,
          event.email,
          event.phone,
          event.password,
        );
        emit(AuthSuccess(user));
      } catch (e) {
        if (e is Failure) {
          emit(AuthError(e.message));
        } else {
          emit(AuthError(e.toString()));
        }
      }
    });
  }
}
