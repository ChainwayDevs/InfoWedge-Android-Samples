# Zebra Decoders

|Decoder name|API name|Param Value</br>(default value is marked with `*`)|
|:--|:--|:--|
|**UPC-A**|decoder_upca|*true, false|
|&nbsp;&nbsp; - Transmit UPC-A Check Digit|decoder_upca_report_check_digit|*true, false|
|&nbsp;&nbsp; - UPC-A Preamble|decoder_upca_preamble|0 - Preamble None</br>*1 - Preamble Sys Char</br>2 - Preamble Country and Sys Char|
|**UPC-E**|decoder_upce|*true, false|
|&nbsp;&nbsp; - Transmit UPC-E Check Digit|decoder_upce_report_check_digit|*true, false|
|&nbsp;&nbsp; - Convert UPC-E to A|decoder_upce_convert_to_upca|true, *false|
|&nbsp;&nbsp; - UPC-E Preamble|decoder_upce_preamble|0 - Preamble None</br>*1 - Preamble Sys Char</br>2 - Preamble Country and Sys Char|
|**UPC-E1**|decoder_upce1|true, *false|
|&nbsp;&nbsp; - Transmit UPC-E1 Check Digit|decoder_upce1_report_check_digit|*true, false|
|&nbsp;&nbsp; - Convert UPC-E1 to A|decoder_upce1_convert_to_upca|true, *false|
|&nbsp;&nbsp; - UPC-E1 Preamble|decoder_upce1_preamble|0 - Preamble None</br>*1 - Preamble Sys Char</br>2 - Preamble Country and Sys Char|
|**EAN-8 / JAN 8**|decoder_ean8|*true, false|
|**EAN-13 / JAN 13**|decoder_ean13|*true, false|
|**Code 128**|decoder_code128|*true, false|
|&nbsp;&nbsp; - Length1|decoder_code128_length1|0–55 (*0)|
|&nbsp;&nbsp; - Length2|decoder_code128_length2|0–55 (*0)|
|&nbsp;&nbsp; - GS1-128|decoder_code128_enable_gs1128|*true, false|
|&nbsp;&nbsp; - ISBT 128|decoder_code128_enable_isbt128|*true, false|
|&nbsp;&nbsp; - ISBT Concatenation|decoder_code128_isbt128_concat_mode|*0 - Concat Mode Never</br>1 - Concat Mode Always</br>2 - Concat Mode Auto|
|&nbsp;&nbsp; - Check ISBT Table|decoder_code128_check_isbt_table|true, *false|
|**Code 39**|decoder_code39|*true, false|
|&nbsp;&nbsp; - Length1|decoder_code39_length1|0–55 (*2)|
|&nbsp;&nbsp; - Length2|decoder_code39_length2|0–55 (*55)|
|&nbsp;&nbsp; - Code 39 Check Digit Verification|decoder_code39_verify_check_digit|true, *false|
|&nbsp;&nbsp; - Trioptic Code 39|decoder_code39_trioptic|true, *false|
|&nbsp;&nbsp; - Convert Code 39 to Code 32|decoder_code39_convert_to_code32|true, *false|
|&nbsp;&nbsp; - Code 32 Prefix|decoder_code39_report_code32_prefix|true, *false|
|&nbsp;&nbsp; - Transmit Code 39 Check Digit|decoder_code39_report_check_digit|true, *false|
|&nbsp;&nbsp; - Code 39 Full ASCII Conversion|decoder_code39_full_ascii|true, *false|
|&nbsp;&nbsp; - Code 39 Reduced Quiet Zone|decoder_code39_enable_marginless_decode|true, *false|
|**Code 93**|decoder_code93|true, *false|
|&nbsp;&nbsp; - Length1|decoder_code93_length1|0–55 (*4)|
|&nbsp;&nbsp; - Length2|decoder_code93_length2|0–55 (*55)|
|**Code 11**|decoder_code11|true, *false|
|&nbsp;&nbsp; - Length1|decoder_code11_length1|0–55 (*4)|
|&nbsp;&nbsp; - Length2|decoder_code11_length2|0–55 (*55)|
|&nbsp;&nbsp; - Check Digit Verification|decoder_code11_verify_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Code 11 Check Digit|decoder_code11_report_check_digit|true, *false|
|**Interleaved 2 of 5**|decoder_i2of5|*true, false|
|&nbsp;&nbsp; - Length1|decoder_i2of5_length1|0–55 (*14)|
|&nbsp;&nbsp; - Length2|decoder_i2of5_length2|0–55 (*0)|
|&nbsp;&nbsp; - Check Digit Verification|decoder_i2of5_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit I 2 of 5 Check Digit|decoder_i2of5_report_check_digit|true, *false|
|**Discrete (Standard) 2 of 5**|decoder_d2of5|true, *false|
|&nbsp;&nbsp; - Length1|decoder_d2of5_length1|0–55 (*12)|
|&nbsp;&nbsp; - Length2|decoder_d2of5_length2|0–55 (*0)|
|**Matrix 2 of 5**|decoder_matrix_2of5|true, *false|
|&nbsp;&nbsp; - Length1|decoder_matrix_2of5_length1|0–55 (*14)|
|&nbsp;&nbsp; - Length2|decoder_matrix_2of5_length2|0–55 (*0)|
|**Codabar**|decoder_codabar|true, *false|
|&nbsp;&nbsp; - Length1|decoder_codabar_length1|0–55 (*5)|
|&nbsp;&nbsp; - Length2|decoder_codabar_length2|0–55 (*55)|
|&nbsp;&nbsp; - CLSI Editing|decoder_codabar_clsi_editing|true, *false|
|&nbsp;&nbsp; - NOTIS Editing|decoder_codabar_notis_editing|true, *false|
|**MSI**|decoder_msi|true, *false|
|&nbsp;&nbsp; - Length1|decoder_msi_length1|0–55 (*4)|
|&nbsp;&nbsp; - Length2|decoder_msi_length2|0–55 (*55)|
|&nbsp;&nbsp; - MSI Check Digits|decoder_msi_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit MSI Check Digit|decoder_msi_report_check_digit|true, *false|
|&nbsp;&nbsp; - MSI Check Digit Algorithm|decoder_msi_check_digit_scheme|*true, false|
|**Chinese 2 of 5**|decoder_chinese_2of5|true, *false|
|**Korean 3 of 5**|decoder_korean_3of5|true, *false|
|**US Planet**|decoder_usplanet|*true, false|
|&nbsp;&nbsp; - Transmit US Planet Check Digit|decoder_usplanet_report_check_digit|*true, false|
|**US Postnet**|decoder_uspostnet|*true, false|
|&nbsp;&nbsp; - Transmit US Postnet Check Digit|decoder_uspostnet_report_check_digit|*true, false|
|**UK Postal**|decoder_us_postal|*true, false|
|**Japan Postal**|decoder_japan_postal|*true, false|
|**Australia Post**|decoder_australia_post|*true, false|
|**GS1 DataBar Expanded**|decoder_gs1_databar_exp|true, *false|
|**GS1 DataBar Limited**|decoder_gs1_databar_lim|true, *false|
|**GS1 DataBar-14**|decoder_gs1_databar14|*true, false|
|**Composite CC-C**|decoder_composite_c|true, *false|
|**Composite CC-A/B**|decoder_composite_ab|true, *false|
|&nbsp;&nbsp; - UPC Composite Mode|decoder_composite_upc_link_mode|*0 - UPC Never Linked</br>1 - UPC Always Linked</br>2 - Auto discriminate UPC Composites|
|**Composite TLC-39**|decoder_composite_tlc39|true, *false|
|**PDF417**|decoder_pdf417|*true, false|
|**MicroPDF417**|decoder_micropdf417|true, *false|
|**Data Matrix**|decoder_datamatrix|*true, false|
|&nbsp;&nbsp; - Data Matrix Inverse|decoder_datamatrix_inverse|*0 - Regular Only</br>1 - Inverse Only</br>2 - Inverse Autodetect|
|&nbsp;&nbsp; - Decode Mirror Images|decoder_datamatrix_mirror|*0 - Never</br>1 - Always</br>2 - Auto|
|**MaxiCode**|decoder_maxicode|*true, false|
|**QR Code**|decoder_qrcode|*true, false|
|**MicroQR**|decoder_microqr|*true, false|
|**Aztec**|decoder_aztec|*true, false|
|&nbsp;&nbsp; - Aztec Inverse|decoder_aztec_inverse|*0 - Regular Only</br>1 - Inverse Only</br>2 - Inverse Autodetect|
|**Han Xin**|decoder_hanxin|true, *false|
|&nbsp;&nbsp; - Han Xin Inverse|decoder_hanxin_inverse|*0 - Regular Only</br>1 - Inverse Only</br>2 - Inverse Autodetect|
|**Grid Matrix**|decoder_grid_matrix|*true, false|
|&nbsp;&nbsp; - Grid Matrix Inverse|decoder_grid_matrix_inverse|*0 - Regular Only</br>1 - Inverse Only</br>2 - Inverse Autodetect|
|&nbsp;&nbsp; - Decode Mirror Images|decoder_grid_matrix_mirror|*0 - Never</br>1 - Always</br>2 - Auto|
