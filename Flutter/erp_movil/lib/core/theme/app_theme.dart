import 'package:flutter/material.dart';
import '../constants/app_colors.dart';
import '../constants/app_sizes.dart';

class AppTheme {
  static ThemeData get lightTheme {
    return ThemeData(
      primaryColor: AppColors.primaryBlue,
      scaffoldBackgroundColor: AppColors.backgroundWhite,
      fontFamily: 'Roboto', // Default font
      appBarTheme: const AppBarTheme(
        backgroundColor: AppColors.primaryBlue,
        elevation: 0,
        centerTitle: true,
        iconTheme: IconThemeData(color: AppColors.pureWhite),
        titleTextStyle: TextStyle(
          color: AppColors.pureWhite,
          fontSize: 20,
          fontWeight: FontWeight.bold,
        ),
      ),
      colorScheme: const ColorScheme.light(
        primary: AppColors.primaryBlue,
        secondary: AppColors.lightBlue,
        surface: AppColors.pureWhite,
        error: AppColors.errorRed,
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.primaryBlue,
          foregroundColor: AppColors.pureWhite,
          padding: const EdgeInsets.symmetric(
            vertical: AppSizes.p16,
            horizontal: AppSizes.p24,
          ),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(AppSizes.radiusMedium),
          ),
          elevation: 2,
        ),
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: AppColors.pureWhite,
        contentPadding: const EdgeInsets.symmetric(
          vertical: AppSizes.p16,
          horizontal: AppSizes.p16,
        ),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(AppSizes.radiusSmall),
          borderSide: const BorderSide(color: AppColors.lightBlue),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(AppSizes.radiusSmall),
          borderSide: BorderSide(color: Colors.grey.shade300),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(AppSizes.radiusSmall),
          borderSide: const BorderSide(color: AppColors.primaryBlue, width: 2),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(AppSizes.radiusSmall),
          borderSide: const BorderSide(color: AppColors.errorRed),
        ),
      ),
      cardTheme: CardThemeData(
        color: AppColors.pureWhite,
        elevation: 2,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AppSizes.radiusMedium),
        ),
        margin: const EdgeInsets.symmetric(
          vertical: AppSizes.p8,
          horizontal: AppSizes.p16,
        ),
      ),
    );
  }
}
