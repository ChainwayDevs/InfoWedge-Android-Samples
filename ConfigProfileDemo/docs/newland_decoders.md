# Newland Decoders

|Decoder name|API name|Param Value</br>(default value is marked with `*`)|
|:--|:--|:--|
|**UPC-A**|decoder_upca|*true, false|
|&nbsp;&nbsp; - Transmit UPC-A Check Digit|decoder_upca_report_check_digit|*true, false|
|&nbsp;&nbsp; - Transmit UPC-A Sys Char|decoder_upca_report_sys_char|*true, false|
|&nbsp;&nbsp; - Digit2|decoder_upca_digit2|true, *false|
|&nbsp;&nbsp; - Digit5|decoder_upca_digit5|true, *false|
|&nbsp;&nbsp; - AddonRequired|decoder_upca_addon_required|true, *false|
|&nbsp;&nbsp; - UsSysData|decoder_upca_report_us_sys_char|true, *false|
|&nbsp;&nbsp; - Only Data|decoder_upca_only_data|*true, false|
|&nbsp;&nbsp; - Coupon|decoder_upca_coupon|true, *false|
|&nbsp;&nbsp; - ReqCoupon|decoder_upca_req_coupon|true, *false|
|&nbsp;&nbsp; - Gs1Coupon|decoder_upca_gs1_coupon|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_upca_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_upca_num_fixed|true, *false|
|**UPC-E**|decoder_upce|*true, false|
|&nbsp;&nbsp; - Transmit UPC-E Check Digit|decoder_upce_report_check_digit|*true, false|
|&nbsp;&nbsp; - Transmit UPC-E Sys Char|decoder_upce_report_sys_char|*true, false|
|&nbsp;&nbsp; - Digit2|decoder_upce_digit2|true, *false|
|&nbsp;&nbsp; - Digit5|decoder_upce_digit5|true, *false|
|&nbsp;&nbsp; - MsgToupca|decoder_upce_msg_to_upca|true, *false|
|&nbsp;&nbsp; - AddonRequired|decoder_upce_addon_required|true, *false|
|&nbsp;&nbsp; - UsSysData|decoder_upce_report_us_sys_char|true, *false|
|&nbsp;&nbsp; - Only Data|decoder_upce_only_data|*true, false|
|&nbsp;&nbsp; - CodeNum|decoder_upce_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_upce_num_fixed|true, *false|
|**EAN-8 / JAN 8**|decoder_ean8|*true, false|
|&nbsp;&nbsp; - Transmit EAN-8 Check Digit|decoder_ean8_report_check_digit|*true, false|
|&nbsp;&nbsp; - Digit2|decoder_ean8_digit2|true, *false|
|&nbsp;&nbsp; - Digit5|decoder_ean8_digit5|true, *false|
|&nbsp;&nbsp; - AddonRequired|decoder_ean8_addon_required|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_ean8_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_ean8_num_fixed|true, *false|
|**EAN-13 / JAN 13**|decoder_ean13|*true, false|
|&nbsp;&nbsp; - Transmit EAN-13 Check Digit|decoder_ean13_report_check_digit|*true, false|
|&nbsp;&nbsp; - Digit2|decoder_ean13_digit2|true, *false|
|&nbsp;&nbsp; - Digit5|decoder_ean13_digit5|true, *false|
|&nbsp;&nbsp; - AddonRequired|decoder_ean13_addon_required|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_ean13_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_ean13_num_fixed|true, *false|
|**Code 128**|decoder_code128|*true, false|
|&nbsp;&nbsp; - Length1|decoder_code128_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_code128_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_code128_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_code128_num_fixed|true, *false|
|**Code 39**|decoder_code39|*true, false|
|&nbsp;&nbsp; - Length1|decoder_code39_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_code39_length2|1-127 (*127)|
|&nbsp;&nbsp; - Code 39 Check Digit Verification|decoder_code39_verify_check_digit|true, *false|
|&nbsp;&nbsp; - Code 32 Prefix|decoder_code39_report_code32_prefix|true, *false|
|&nbsp;&nbsp; - Transmit Code 39 Check Digit|decoder_code39_report_check_digit|true, *false|
|&nbsp;&nbsp; - Code 39 Full ASCII Conversion|decoder_code39_full_ascii|true, *false|
|&nbsp;&nbsp; - TrsmtStartStop|decoder_code39_report_start_stop|true, *false|
|&nbsp;&nbsp; - Code32SpecEdit|decoder_code39_code32_spec_edit|true, *false|
|&nbsp;&nbsp; - Code32TrsmtChkChar|decoder_code39_code32_report_check_digit|true, *false|
|&nbsp;&nbsp; - Code32TrsmtStasrtStop|decoder_code39_code32_report_start_stop|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_code39_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_code39_num_fixed|true, *false|
|**Code 93**|decoder_code93|true, *false|
|&nbsp;&nbsp; - Length1|decoder_code93_length1|1-127 (*2)|
|&nbsp;&nbsp; - Length2|decoder_code93_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_code93_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_code93_num_fixed|true, *false|
|**Code 11**|decoder_code11|true, *false|
|&nbsp;&nbsp; - Length1|decoder_code11_length1|1-127 (*6)|
|&nbsp;&nbsp; - Length2|decoder_code11_length2|1-127 (*127)|
|&nbsp;&nbsp; - Transmit Code 11 Check Digit|decoder_code11_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_code11_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_code11_num_fixed|true, *false|
|&nbsp;&nbsp; - ChkMode|decoder_code11_chk_mode|*0 - OFF</br>1 - C11_MOD11</br>2 - C11_FIXED_MOD11_MOD11</br>3 - C11_FIXED_MOD11_MOD9</br>4 - C11_AUTO_MOD11_MOD11</br>5 - C11_C11_AUTO_MOD11_MOD9|
|**Interleaved 2 of 5**|decoder_i2of5|*true, false|
|&nbsp;&nbsp; - Length1|decoder_i2of5_length1|1-127 (*6)|
|&nbsp;&nbsp; - Length2|decoder_i2of5_length2|1-127 (*127)|
|&nbsp;&nbsp; - Check Digit Verification|decoder_i2of5_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit I 2 of 5 Check Digit|decoder_i2of5_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_i2of5_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_i2of5_num_fixed|true, *false|
|**Matrix 2 of 5**|decoder_matrix_2of5|true, *false|
|&nbsp;&nbsp; - Length1|decoder_matrix_2of5_length1|1-127 (*6)|
|&nbsp;&nbsp; - Length2|decoder_matrix_2of5_length2|1-127 (*127)|
|&nbsp;&nbsp; - Check Digit Verification|decoder_matrix_2of5_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_matrix_2of5_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_matrix_2of5_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_matrix_2of5_num_fixed|true, *false|
|**Codabar**|decoder_codabar|*true, false|
|&nbsp;&nbsp; - Length1|decoder_codabar_length1|1-127 (*4)|
|&nbsp;&nbsp; - Length2|decoder_codabar_length2|1-127 (*127)|
|&nbsp;&nbsp; - TrsmtStartStop|decoder_codabar_report_start_stop|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_codabar_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_codabar_num_fixed|true, *false|
|**US Planet**|decoder_usplanet|true, *false|
|&nbsp;&nbsp; - Transmit US Planet Check Digit|decoder_usplanet_report_check_digit|true, *false|
|**US Postnet**|decoder_uspostnet|true, *false|
|&nbsp;&nbsp; - Transmit US Postnet Check Digit|decoder_uspostnet_report_check_digit|true, *false|
|**Japan Postal**|decoder_japan_postal|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_japan_postal_report_check_digit|true, *false|
|**Australia Post**|decoder_australia_post|*true, false|
|**PDF417**|decoder_pdf417|*true, false|
|&nbsp;&nbsp; - Length1|decoder_pdf417_length1|1-2710 (*1)|
|&nbsp;&nbsp; - Length2|decoder_pdf417_length2|1-2710 (*2710)|
|&nbsp;&nbsp; - CodeNum|decoder_pdf417_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_pdf417_num_fixed|true, *false|
|&nbsp;&nbsp; - Inverse|decoder_pdf417_inverse|*0 - Normal</br>1 - Inverse</br>2 - Inversion Mode|
|&nbsp;&nbsp; - Mirror|decoder_pdf417_mirror_en|*true, false|
|&nbsp;&nbsp; - CloseECI|decoder_pdf417_close_eci|*true, false|
|**MicroPDF417**|decoder_micropdf417|true, *false|
|&nbsp;&nbsp; - Length1|decoder_micropdf417_length1|1-366 (*1)|
|&nbsp;&nbsp; - Length2|decoder_micropdf417_length2|1-366 (*366)|
|&nbsp;&nbsp; - CodeNum|decoder_micropdf417_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_micropdf417_num_fixed|true, *false|
|&nbsp;&nbsp; - Mirror|decoder_micropdf417_mirror_en|*true, false|
|&nbsp;&nbsp; - CloseECI|decoder_micropdf417_close_eci|*true, false|
|**Data Matrix**|decoder_datamatrix|*true, false|
|&nbsp;&nbsp; - Data Matrix Inverse|decoder_datamatrix_inverse|*0 - Normal</br>1 - Inverse</br>2 - Inversion Mode|
|&nbsp;&nbsp; - Length1|decoder_datamatrix_length1|1-3116 (*1)|
|&nbsp;&nbsp; - Length2|decoder_datamatrix_length2|1-3116 (*3116)|
|&nbsp;&nbsp; - CodeNum|decoder_datamatrix_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_datamatrix_num_fixed|true, *false|
|&nbsp;&nbsp; - Mirror|decoder_datamatrix_mirror_en|*true, false|
|&nbsp;&nbsp; - CloseECI|decoder_datamatrix_close_eci|*true, false|
|&nbsp;&nbsp; - RectAngle|decoder_datamatrix_rect_angle|true, *false|
|**MaxiCode**|decoder_maxicode|true, *false|
|&nbsp;&nbsp; - Length1|decoder_maxicode_length1|1-150 (*1)|
|&nbsp;&nbsp; - Length2|decoder_maxicode_length2|1-150 (*150)|
|&nbsp;&nbsp; - CodeNum|decoder_maxicode_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_maxicode_num_fixed|true, *false|
|&nbsp;&nbsp; - Mirror|decoder_maxicode_mirror_en|*true, false|
|**QR Code**|decoder_qrcode|*true, false|
|&nbsp;&nbsp; - Length1|decoder_qrcode_length1|1-7089 (*1)|
|&nbsp;&nbsp; - Length2|decoder_qrcode_length2|1-7089 (*7089)|
|&nbsp;&nbsp; - Inverse|decoder_qrcode_inverse|*0 - Normal</br>1 - Inverse</br>2 - Inversion Mode|
|&nbsp;&nbsp; - CloseECI|decoder_qrcode_close_eci|*true, false|
|&nbsp;&nbsp; - CodeNum|decoder_qrcode_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_qrcode_num_fixed|true, *false|
|&nbsp;&nbsp; - Model1|decoder_qrcode_model1|*true, false|
|&nbsp;&nbsp; - Mirror|decoder_qrcode_mirror_en|*true, false|
|**MicroQR**|decoder_microqr|*true, false|
|&nbsp;&nbsp; - Length1|decoder_microqr_length1|1-35 (*1)|
|&nbsp;&nbsp; - Length2|decoder_microqr_length2|1-35 (*35)|
|&nbsp;&nbsp; - CodeNum|decoder_microqr_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_microqr_num_fixed|true, *false|
|&nbsp;&nbsp; - Mirror|decoder_microqr_mirror_en|*true, false|
|**Aztec**|decoder_aztec|true, *false|
|&nbsp;&nbsp; - Aztec Inverse|decoder_aztec_inverse|*0 - Normal</br>1 - Inverse</br>2 - Inversion Mode|
|&nbsp;&nbsp; - Length1|decoder_aztec_length1|1-3832 (*1)|
|&nbsp;&nbsp; - Length2|decoder_aztec_length2|1-3832 (*3832)|
|&nbsp;&nbsp; - CodeNum|decoder_aztec_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_aztec_num_fixed|true, *false|
|&nbsp;&nbsp; - Mirror|decoder_aztec_mirror|*true, false|
|&nbsp;&nbsp; - CloseECI|decoder_aztec_close_eci|*true, false|
|**Han Xin**|decoder_hanxin|true, *false|
|&nbsp;&nbsp; - Han Xin Inverse|decoder_hanxin_inverse|*0 - Normal</br>1 - Inverse</br>2 - Inversion Mode|
|&nbsp;&nbsp; - Length1|decoder_hanxin_length1|1-7827 (*1)|
|&nbsp;&nbsp; - Length2|decoder_hanxin_length2|1-7827 (*7827)|
|&nbsp;&nbsp; - CodeNum|decoder_hanxin_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_hanxin_num_fixed|true, *false|
|&nbsp;&nbsp; - Mirror|decoder_hanxin_mirror|*true, false|
|&nbsp;&nbsp; - CloseECI|decoder_hanxin_close_eci|*true, false|
|**China Post**|decoder_china_post|true, *false|
|&nbsp;&nbsp; - Length1|decoder_china_post_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_china_post_length2|1-127 (*127)|
|&nbsp;&nbsp; - Check Digit Verification|decoder_china_post_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_china_post_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_china_post_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_china_post_num_fixed|true, *false|
|**MSI Plessey**|decoder_msi_plessey|true, *false|
|&nbsp;&nbsp; - Length1|decoder_msi_plessey_length1|1-127 (*4)|
|&nbsp;&nbsp; - Length2|decoder_msi_plessey_length2|1-127 (*127)|
|&nbsp;&nbsp; - Transmit MSI Check Digit|decoder_msi_plessey_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_msi_plessey_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_msi_plessey_num_fixed|true, *false|
|&nbsp;&nbsp; - ChkMode|decoder_msi_plessey_chk_mode|*0 - OFF</br>1 - MOD10</br>2 - MOD10MOD10</br>3 - MOD10MOD11|
|**AIM 128**|decoder_aim128|true, *false|
|&nbsp;&nbsp; - Length1|decoder_aim128_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_aim128_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_aim128_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_aim128_num_fixed|true, *false|
|**Code 16K**|decoder_code16k|true, *false|
|&nbsp;&nbsp; - Length1|decoder_code16k_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_code16k_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_code16k_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_code16k_num_fixed|true, *false|
|**Code 49**|decoder_code49|true, *false|
|&nbsp;&nbsp; - Length1|decoder_code49_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_code49_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_code49_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_code49_num_fixed|true, *false|
|**Industrial 2 of 5**|decoder_industrial_2of5|true, *false|
|&nbsp;&nbsp; - Check Digit Verification|decoder_industrial_2of5_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_industrial_2of5_report_check_digit|true, *false|
|&nbsp;&nbsp; - Length1|decoder_industrial_2of5_length1|1-127 (*6)|
|&nbsp;&nbsp; - Length2|decoder_industrial_2of5_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_industrial_2of5_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_industrial_2of5_num_fixed|true, *false|
|**ISBN**|decoder_isbn|true, *false|
|&nbsp;&nbsp; - Length|decoder_isbn_length|*0 - 10DIGIT</br>1 - 13DIGIT|
|&nbsp;&nbsp; - Digit2|decoder_isbn_digit2|true, *false|
|&nbsp;&nbsp; - Digit5|decoder_isbn_digit5|true, *false|
|&nbsp;&nbsp; - AddonRequired|decoder_isbn_addon_required|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_isbn_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_isbn_num_fixed|true, *false|
|**ISSN**|decoder_issn|true, *false|
|&nbsp;&nbsp; - Digit2|decoder_issn_digit2|true, *false|
|&nbsp;&nbsp; - Digit5|decoder_issn_digit5|true, *false|
|&nbsp;&nbsp; - AddonRequired|decoder_issn_addon_required|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_issn_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_issn_num_fixed|true, *false|
|**ITF-14**|decoder_itf14|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_itf14_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_itf14_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_itf14_num_fixed|true, *false|
|**ITF-6**|decoder_itf6|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_itf6_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_itf6_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_itf6_num_fixed|true, *false|
|**UK Plessey**|decoder_uk_plessey|true, *false|
|&nbsp;&nbsp; - Length1|decoder_uk_plessey_length1|1-127 (*2)|
|&nbsp;&nbsp; - Length2|decoder_uk_plessey_length2|1-127 (*127)|
|&nbsp;&nbsp; - Check Digits|decoder_uk_plessey_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_uk_plessey_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_uk_plessey_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_uk_plessey_num_fixed|true, *false|
|**RSS**|decoder_rss|*true, false|
|&nbsp;&nbsp; - Transmit Ai|decoder_rss_report_ai|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_rss_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_rss_num_fixed|true, *false|
|**Standard 2 of 5**|decoder_standard_2of5|true, *false|
|&nbsp;&nbsp; - Check Digit Verification|decoder_standard_2of5_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_standard_2of5_report_check_digit|true, *false|
|&nbsp;&nbsp; - Length1|decoder_standard_2of5_length1|1-127 (*6)|
|&nbsp;&nbsp; - Length2|decoder_standard_2of5_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_standard_2of5_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_standard_2of5_num_fixed|true, *false|
|**UCC/EAN-128**|decoder_ean128|*true, false|
|&nbsp;&nbsp; - Length1|decoder_ean128_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_ean128_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_ean128_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_ean128_num_fixed|true, *false|
|**Grid Matrix**|decoder_grid_matrix|true, *false|
|&nbsp;&nbsp; - Length1|decoder_grid_matrix_length1|1-2751 (*1)|
|&nbsp;&nbsp; - Length2|decoder_grid_matrix_length2|1-2751 (*2751)|
|&nbsp;&nbsp; - CloseECI|decoder_grid_matrix_close_eci|*true, false|
|**DotCode**|decoder_dotcode|*true, false|
|**China Post**|decoder_china_post|true, *false|
|&nbsp;&nbsp; - Check Digit Verification|decoder_china_post_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_china_post_report_check_digit|true, *false|
|&nbsp;&nbsp; - Length1|decoder_china_post_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_china_post_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_china_post_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_china_post_num_fixed|true, *false|
|**USPS Intelligent Mail**|decoder_usps_itlgt_mail|true, *false|
|**KIX Code**|decoder_kixcode|true, *false|
|**Royal Mail Customer Bar Code**|decoder_rm4scc|true, *false|
|**OCR**|decoder_ocr|true, *false|