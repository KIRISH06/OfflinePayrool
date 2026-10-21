# OfflinePay (Android, Kotlin + Compose)

Sab kuch app ke andar: QR scan, UPI ID / mobile number se pay, balance, history.
App *99# ki baat SIM se khud karta hai; system dialog nahi khulta. UPI PIN app ke PIN box mein.

## APK banane ka aasaan tarika (PC ke bina)
1. github.com par free account -> New repository -> "Upload files" se is folder ki saari files daalein
   (.github folder samet).
2. Repo mein "Actions" tab -> "Build APK" -> Run workflow.
3. 5-10 min baad run kholein -> neeche "Artifacts" -> OfflinePay-apk download karein -> phone mein install.

## Ya Android Studio se
Folder open karein -> Build -> Build APK(s).

## Dhyan rakhein
- Asli phone par chalega (emulator par nahi). SIM mein signal + bank-registered number chahiye.
- Limit ~Rs 5,000, ~Rs 0.50 telecom charge.
- Kuch phone/operator par in-app USSD session aage nahi badhta; tab Settings -> Compatibility mode ON karein.
- Menu ka text bank ke hisaab se alag hota hai; na pehchana jaye to app wo menu aapko app ke andar dikha deta hai.
