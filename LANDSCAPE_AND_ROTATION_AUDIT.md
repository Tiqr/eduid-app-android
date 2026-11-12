# Landscape & Rotation Audit

Device: Samsung (R5CW41MBBRW), debug build `nl.eduid.testing`. Debug source set excluded.
Method: device testing (landscape, keyboard, rotation) plus static review. No Gradle was run and no code was changed.

## 1. Landscape and IME problems

### Confirmed on the device

| Screen                          | Problem                                                                                                                      |
|---------------------------------|------------------------------------------------------------------------------------------------------------------------------|
| HomePageWithAccountContent      | Not scrollable. The illustration covers the title, the bottom is clipped, and the 4 bottom tabs are squashed to about 67 px. |
| ManageAccountScreen             | Not scrollable. The buttons are squashed and "Delete your eduID" is cut off.                                                 |
| EditEmailScreen                 | Not scrollable. The keyboard hides Save and the title ends up under the status bar.                                          |
| EditNameFormScreen              | Not scrollable. With the keyboard open only the first name is visible. The last name and Save/Cancel are unreachable.        |
| DeleteAccountFirstConfirmScreen | The "Delete your account" button is clipped at the bottom. The screen is scrollable.                                         |
| VerifyWithIdIntroScreen         | Cramped. The scroll area is only about 630 px because the button is pinned.                                                  |

### Static review only (not tested on the device)

- **Probable clipping (no scroll container):** HomePageNoAccount, WelcomeStartScreen, RequestEduIdEmailSentScreen, DeepLinkScreen,
  ContinueInBrowserScreen.
- **Probable IME problems (text input, no scroll):** DeleteAccountSecondConfirmScreen, PhoneRequestCodeScreen, ConfirmCodeScreen,
  ChangePhoneVerifyPinScreen, OneTimePasswordScreen, RegistrationPinSetupScreen/PinContent, EmailCodeEntryScreen,
  AuthenticationPinBiometricScreen.
- **Other screens without a scroll container:** AuthenticationCompletedScreen, RequestAuthenticationScreen, RequestEduIdCreatedScreen,
  DeletePasskeyScreen, OAuthScreen, RemoveMobileAppScreen, ResetPasswordScreen, ScanScreen, SplashScreen, TwoFactorKeyDeleteScreen.

### OK on the device

PersonalInfo, Security, Activity, VerifyIdentity, SelectYourBank, VerifyWithIdInput.

### Not reached on the device

The not-logged-in flow (welcome, request eduID, scan, authorize) and the Security sub-flows.

**Fix:** add `verticalScroll`, `imePadding()` and navigation-bar padding to these screens. On the home screen, use a landscape layout or
make it scrollable.

## 2. Rotation data loss

| Location                                                                       | Lost on rotation                                                                     |
|--------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `screens/verifywithid/input/VerifyWithIdInputScreen.kt:91-93`                  | First name, last name and date of birth (confirmed).                                 |
| `screens/homepage/HomePageWithAccountContent.kt:87`, `HomePageNoAccount.kt:73` | The AboutInfo bottom sheet closes (confirmed).                                       |
| `HomePageWithAccountContent.kt:191`                                            | `clickCount` resets.                                                                 |
| `screens/emailcodeentry/EmailCodeEntryScreen.kt:144,146`                       | The keyboard and resend-option flags reset.                                          |
| `screens/authorize/AuthenticationPinBiometricScreen.kt:101,174`                | `hasAutoRequestedBiometrics` resets, so the biometric prompt can be requested again. |
| `screens/selectyourbank/SelectYourBankScreen.kt:168`                           | `isFetchingLink` resets.                                                             |
| `ui/ConnectionCard.kt:70,182`, `ui/InfoField.kt:299`, `ui/LoginInfoCard.kt:49` | Cards collapse again.                                                                |
| `ui/AlertDialog.kt:28,68,120,159`                                              | `openDialog` is a plain `remember`, so a dismissed dialog reopens.                   |

### ViewModels

- 17 of the 33 `@HiltViewModel` classes use `SavedStateHandle`.
- The others (e.g. `SelectYourBankViewModel`, `DeleteAccountSecondConfirmViewModel`, `HomePageViewModel`) keep `uiState` in a plain
  `mutableStateOf`. A ViewModel survives rotation, so this only matters after process death.
- `OAuthViewModel`, `VerifyWithIdCodeViewModel` and `RegistrationPinSetupViewModel` have `SavedStateHandle`, but I didn't check which of
  their fields actually go into it.

**Fix:** switch the plain `remember` states above to `rememberSaveable`. For the ViewModels, store in-progress input and step state in
`SavedStateHandle`.
