import 'package:get_it/get_it.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'features/auth/data/repositories/auth_repository_impl.dart';
import 'core/network/api_client.dart';
import 'features/auth/domain/repositories/auth_repository.dart';
import 'features/auth/domain/usecases/login_usecase.dart';
import 'features/auth/presentation/bloc/auth_bloc.dart';

final sl = GetIt.instance; // sl = Service Locator

Future<void> init() async {
  //! External
  final sharedPreferences = await SharedPreferences.getInstance();
  
  // Inyectar usuarios de prueba si no existen
  if (!sharedPreferences.containsKey('user_admin@erp.com')) {
    const adminJson = '{"id":"1","name":"Juan","surname":"Pérez (Admin)","email":"admin@erp.com","phone":"123456789"}';
    await sharedPreferences.setString('user_admin@erp.com', adminJson);
  }
  if (!sharedPreferences.containsKey('user_empleado@erp.com')) {
    const empleadoJson = '{"id":"2","name":"María","surname":"Gómez (Empleado)","email":"empleado@erp.com","phone":"987654321"}';
    await sharedPreferences.setString('user_empleado@erp.com', empleadoJson);
  }

  sl.registerLazySingleton(() => sharedPreferences);
  sl.registerLazySingleton(() => ApiClient(sharedPreferences: sl()));

  //! Features - Auth
  // Bloc
  sl.registerFactory(
    () => AuthBloc(
      loginUseCase: sl(),
    ),
  );

  // Use cases
  sl.registerLazySingleton(() => LoginUseCase(sl()));

  // Repository
  sl.registerLazySingleton<AuthRepository>(
    () => AuthRepositoryImpl(apiClient: sl(), sharedPreferences: sl()),
  );
}
