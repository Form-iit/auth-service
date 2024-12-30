package com.example.authservice.mailTemplates.templates;

import org.springframework.stereotype.Component;

@Component
public class mailTemplates {
  public static String getRegistrationEmailTemplate() {
    return """
<!--
* This email was built using Tabular.
* For more information, visit https://tabular.email
-->
<!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml"
    xmlns:o="urn:schemas-microsoft-com:office:office" lang="en">

<head>
    <title></title>
    <meta charset="UTF-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <!--[if !mso]>-->
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <!--<![endif]-->
    <meta name="x-apple-disable-message-reformatting" content="" />
    <meta content="target-densitydpi=device-dpi" name="viewport" />
    <meta content="true" name="HandheldFriendly" />
    <meta content="width=device-width" name="viewport" />
    <meta name="format-detection" content="telephone=no, date=no, address=no, email=no, url=no" />
    <style type="text/css">
        table {
            border-collapse: separate;
            table-layout: fixed;
            mso-table-lspace: 0pt;
            mso-table-rspace: 0pt
        }

        table td {
            border-collapse: collapse
        }

        .ExternalClass {
            width: 100%
        }

        .ExternalClass,
        .ExternalClass p,
        .ExternalClass span,
        .ExternalClass font,
        .ExternalClass td,
        .ExternalClass div {
            line-height: 100%
        }

        .gmail-mobile-forced-width {
            display: none;
            display: none !important;
        }

        body,
        a,
        li,
        p,
        h1,
        h2,
        h3 {
            -ms-text-size-adjust: 100%;
            -webkit-text-size-adjust: 100%;
        }

        html {
            -webkit-text-size-adjust: none !important
        }

        body,
        #innerTable {
            -webkit-font-smoothing: antialiased;
            -moz-osx-font-smoothing: grayscale
        }

        #innerTable img+div {
            display: none;
            display: none !important
        }

        img {
            Margin: 0;
            padding: 0;
            -ms-interpolation-mode: bicubic
        }

        h1,
        h2,
        h3,
        p,
        a {
            line-height: inherit;
            overflow-wrap: normal;
            white-space: normal;
            word-break: break-word
        }

        a {
            text-decoration: none
        }

        h1,
        h2,
        h3,
        p {
            min-width: 100% !important;
            width: 100% !important;
            max-width: 100% !important;
            display: inline-block !important;
            border: 0;
            padding: 0;
            margin: 0
        }

        a[x-apple-data-detectors] {
            color: inherit !important;
            text-decoration: none !important;
            font-size: inherit !important;
            font-family: inherit !important;
            font-weight: inherit !important;
            line-height: inherit !important
        }

        u+#body a {
            color: inherit;
            text-decoration: none;
            font-size: inherit;
            font-family: inherit;
            font-weight: inherit;
            line-height: inherit;
        }

        a[href^="mailto"],
        a[href^="tel"],
        a[href^="sms"] {
            color: inherit;
            text-decoration: none
        }
    </style>
    <style type="text/css">
        @media (min-width: 481px) {
            .hd {
                display: none !important
            }
        }
    </style>
    <style type="text/css">
        @media (max-width: 480px) {
            .hm {
                display: none !important
            }
        }
    </style>
    <style type="text/css">
        @media (max-width: 480px) {

            .t1,
            .t13,
            .t19,
            .t21,
            .t5,
            .t9 {
                width: 420px !important
            }

            .t21 {
                padding-bottom: 70px !important
            }

            .t3 {
                mso-line-height-alt: 28px !important;
                line-height: 28px !important
            }

            .t1 {
                padding-bottom: 28px !important
            }

            .t25,
            .t29,
            .t32,
            .t35 {
                width: 450px !important
            }

            .t0 {
                line-height: 40px !important;
                font-size: 32px !important;
                mso-text-raise: 2px !important
            }

            .t15 {
                mso-line-height-alt: 30px !important;
                line-height: 30px !important
            }

            .t12,
            .t4,
            .t8 {
                line-height: 26px !important;
                font-size: 16px !important
            }

            .t16,
            .t17 {
                line-height: 46px !important;
                mso-text-raise: 10px !important
            }

            .t16 {
                font-size: 12px !important
            }

            .t35 {
                padding-top: 60px !important;
                padding-bottom: 60px !important;
                padding-right: 30px !important
            }
        }
    </style>
    <style type="text/css">
        @media (max-width: 480px) {
            [class~="x_t21"] {
                padding-bottom: 70px !important;
                width: 420px !important;
            }

            [class~="x_t19"] {
                width: 420px !important;
            }

            [class~="x_t3"] {
                mso-line-height-alt: 28px !important;
                line-height: 28px !important;
            }

            [class~="x_t1"] {
                padding-bottom: 28px !important;
                width: 420px !important;
            }

            [class~="x_t0"] {
                line-height: 40px !important;
                font-size: 32px !important;
                mso-text-raise: 2px !important;
            }

            [class~="x_t15"] {
                mso-line-height-alt: 30px !important;
                line-height: 30px !important;
            }

            [class~="x_t13"] {
                width: 420px !important;
            }

            [class~="x_t12"] {
                line-height: 26px !important;
                font-size: 16px !important;
            }

            [class~="x_t17"] {
                line-height: 46px !important;
                mso-text-raise: 10px !important;
            }

            [class~="x_t16"] {
                line-height: 46px !important;
                font-size: 12px !important;
                mso-text-raise: 10px !important;
            }

            [class~="x_t35"] {
                padding-top: 60px !important;
                padding-bottom: 60px !important;
                padding-right: 30px !important;
                width: 450px !important;
            }

            [class~="x_t9"] {
                width: 420px !important;
            }

            [class~="x_t8"] {
                line-height: 26px !important;
                font-size: 16px !important;
            }

            [class~="x_t5"] {
                width: 420px !important;
            }

            [class~="x_t4"] {
                line-height: 26px !important;
                font-size: 16px !important;
            }

            [class~="x_t25"] {
                width: 450px !important;
            }

            [class~="x_t32"] {
                width: 450px !important;
            }

            [class~="x_t29"] {
                width: 450px !important;
            }
        }
    </style>
    <!--[if !mso]>-->
    <link
        href="https://fonts.googleapis.com/css2?family=Fira+Sans:wght@400;700&amp;family=Montserrat:wght@800&amp;display=swap"
        rel="stylesheet" type="text/css" />
    <!--<![endif]-->
    <!--[if mso]>
<xml>
<o:OfficeDocumentSettings>
<o:AllowPNG/>
<o:PixelsPerInch>96</o:PixelsPerInch>
</o:OfficeDocumentSettings>
</xml>
<![endif]-->
</head>

<body id="body" class="t39" style="min-width:100%;Margin:0px;padding:0px;background-color:#EDEDED;">
    <div class="t38" style="background-color:#EDEDED;">
        <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" align="center">
            <tr>
                <td class="t37" style="font-size:0;line-height:0;mso-line-height-rule:exactly;background-color:#EDEDED;"
                    valign="top" align="center">
                    <!--[if mso]>
<v:background xmlns:v="urn:schemas-microsoft-com:vml" fill="true" stroke="false">
<v:fill color="#EDEDED"/>
</v:background>
<![endif]-->
                    <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" align="center"
                        id="innerTable">
                        <tr>
                            <td align="center">
                                <table class="t22" role="presentation" cellpadding="0" cellspacing="0"
                                    style="Margin-left:auto;Margin-right:auto;">
                                    <tr>
                                        <!--[if mso]>
<td width="680" class="t21" style="background-color:#FFFFFF;padding:43px 30px 41px 30px;">
<![endif]-->
                                        <!--[if !mso]>-->
                                        <td class="t21"
                                            style="background-color:#FFFFFF;width:620px;padding:43px 30px 41px 30px;">
                                            <!--<![endif]-->
                                            <table role="presentation" width="100%" cellpadding="0" cellspacing="0"
                                                style="width:100% !important;">
                                                <tr>
                                                    <td align="center">
                                                        <table class="t20" role="presentation" cellpadding="0"
                                                            cellspacing="0" style="Margin-left:auto;Margin-right:auto;">
                                                            <tr>
                                                                <!--[if mso]>
<td width="475" class="t19" style="background-color:transparent;">
<![endif]-->
                                                                <!--[if !mso]>-->
                                                                <td class="t19"
                                                                    style="background-color:transparent;width:475px;">
                                                                    <!--<![endif]-->
                                                                    <table role="presentation" width="100%"
                                                                        cellpadding="0" cellspacing="0"
                                                                        style="width:100% !important;">
                                                                        <tr>
                                                                            <td align="center">
                                                                                <table class="t2" role="presentation"
                                                                                    cellpadding="0" cellspacing="0"
                                                                                    style="Margin-left:auto;Margin-right:auto;">
                                                                                    <tr>
                                                                                        <!--[if mso]>
<td width="475" class="t1" style="border-bottom:1px solid #E1E2E6;padding:0 0 38px 0;">
<![endif]-->
                                                                                        <!--[if !mso]>-->
                                                                                        <td class="t1"
                                                                                            style="border-bottom:1px solid #E1E2E6;width:475px;padding:0 0 38px 0;">
                                                                                            <!--<![endif]-->
                                                                                            <h1 class="t0"
                                                                                                style="margin:0;Margin:0;font-family:Fira Sans,BlinkMacSystemFont,Segoe UI,Helvetica Neue,Arial,sans-serif;line-height:52px;font-weight:700;font-style:normal;font-size:48px;text-decoration:none;text-transform:none;direction:ltr;color:#000000;text-align:left;mso-line-height-rule:exactly;mso-text-raise:1px;">
                                                                                                Confirm your email
                                                                                                address</h1>
                                                                                        </td>
                                                                                    </tr>
                                                                                </table>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>
                                                                                <div class="t3"
                                                                                    style="mso-line-height-rule:exactly;mso-line-height-alt:35px;line-height:35px;font-size:1px;display:block;">
                                                                                    &nbsp;&nbsp;</div>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td align="center">
                                                                                <table class="t6" role="presentation"
                                                                                    cellpadding="0" cellspacing="0"
                                                                                    style="Margin-left:auto;Margin-right:auto;">
                                                                                    <tr>
                                                                                        <!--[if mso]>
<td width="475" class="t5">
<![endif]-->
                                                                                        <!--[if !mso]>-->
                                                                                        <td class="t5"
                                                                                            style="width:475px;">
                                                                                            <!--<![endif]-->
                                                                                            <p class="t4"
                                                                                                style="margin:0;Margin:0;font-family:Fira Sans,BlinkMacSystemFont,Segoe UI,Helvetica Neue,Arial,sans-serif;line-height:28px;font-weight:400;font-style:normal;font-size:18px;text-decoration:none;text-transform:none;direction:ltr;color:#9095A2;text-align:left;mso-line-height-rule:exactly;mso-text-raise:3px;">
                                                                                                Welcome, {{userFirstName}}!!</p>
                                                                                        </td>
                                                                                    </tr>
                                                                                </table>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>
                                                                                <div class="t7"
                                                                                    style="mso-line-height-rule:exactly;mso-line-height-alt:11px;line-height:11px;font-size:1px;display:block;">
                                                                                    &nbsp;&nbsp;</div>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td align="center">
                                                                                <table class="t10" role="presentation"
                                                                                    cellpadding="0" cellspacing="0"
                                                                                    style="Margin-left:auto;Margin-right:auto;">
                                                                                    <tr>
                                                                                        <!--[if mso]>
<td width="475" class="t9">
<![endif]-->
                                                                                        <!--[if !mso]>-->
                                                                                        <td class="t9"
                                                                                            style="width:475px;">
                                                                                            <!--<![endif]-->
                                                                                            <p class="t8"
                                                                                                style="margin:0;Margin:0;font-family:Fira Sans,BlinkMacSystemFont,Segoe UI,Helvetica Neue,Arial,sans-serif;line-height:28px;font-weight:400;font-style:normal;font-size:18px;text-decoration:none;text-transform:none;direction:ltr;color:#9095A2;text-align:left;mso-line-height-rule:exactly;mso-text-raise:3px;">
                                                                                                We are so pleased to
                                                                                                count you in for the
                                                                                                ride. One final step to
                                                                                                become an active member
                                                                                                and start shaping your
                                                                                                ideas into a
                                                                                                reality&nbsp;&nbsp;</p>
                                                                                        </td>
                                                                                    </tr>
                                                                                </table>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>
                                                                                <div class="t11"
                                                                                    style="mso-line-height-rule:exactly;mso-line-height-alt:11px;line-height:11px;font-size:1px;display:block;">
                                                                                    &nbsp;&nbsp;</div>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td align="center">
                                                                                <table class="t14" role="presentation"
                                                                                    cellpadding="0" cellspacing="0"
                                                                                    style="Margin-left:auto;Margin-right:auto;">
                                                                                    <tr>
                                                                                        <!--[if mso]>
<td width="475" class="t13">
<![endif]-->
                                                                                        <!--[if !mso]>-->
                                                                                        <td class="t13"
                                                                                            style="width:475px;">
                                                                                            <!--<![endif]-->
                                                                                            <p class="t12"
                                                                                                style="margin:0;Margin:0;font-family:Fira Sans,BlinkMacSystemFont,Segoe UI,Helvetica Neue,Arial,sans-serif;line-height:28px;font-weight:400;font-style:normal;font-size:18px;text-decoration:none;text-transform:none;direction:ltr;color:#9095A2;text-align:left;mso-line-height-rule:exactly;mso-text-raise:3px;">
                                                                                                Please tap the button
                                                                                                below to verify your
                                                                                                email address.</p>
                                                                                        </td>
                                                                                    </tr>
                                                                                </table>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>
                                                                                <div class="t15"
                                                                                    style="mso-line-height-rule:exactly;mso-line-height-alt:28px;line-height:28px;font-size:1px;display:block;">
                                                                                    &nbsp;&nbsp;</div>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td align="left">
                                                                                <table class="t18" role="presentation"
                                                                                    cellpadding="0" cellspacing="0"
                                                                                    style="Margin-right:auto;">
                                                                                    <tr>
                                                                                        <!--[if mso]>
<td width="246" class="t17" style="background-color:#34D399;overflow:hidden;text-align:center;line-height:48px;mso-line-height-rule:exactly;mso-text-raise:11px;border-radius:40px 40px 40px 40px;">
<![endif]-->
                                                                                        <!--[if !mso]>-->
                                                                                        <td class="t17"
                                                                                            style="background-color:#34D399;overflow:hidden;width:246px;text-align:center;line-height:48px;mso-line-height-rule:exactly;mso-text-raise:11px;border-radius:40px 40px 40px 40px;">
                                                                                            <!--<![endif]-->
                                                                                            <a class="t16"
                                                                                                href="{{verifyEmailAPI}}"
                                                                                                style="display:block;margin:0;Margin:0;font-family:Montserrat,BlinkMacSystemFont,Segoe UI,Helvetica Neue,Arial,sans-serif;line-height:48px;font-weight:800;font-style:normal;font-size:13px;text-decoration:none;text-transform:uppercase;letter-spacing:0.5px;direction:ltr;color:#FFFFFF;text-align:center;mso-line-height-rule:exactly;mso-text-raise:11px;"
                                                                                                target="_blank">verify
                                                                                                your Email address</a>
                                                                                        </td>
                                                                                    </tr>
                                                                                </table>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td align="center">
                                <table class="t36" role="presentation" cellpadding="0" cellspacing="0"
                                    style="Margin-left:auto;Margin-right:auto;">
                                    <tr>
                                        <!--[if mso]>
<td width="682" class="t35" style="background-color:#292929;">
<![endif]-->
                                        <!--[if !mso]>-->
                                        <td class="t35" style="background-color:#292929;width:682px;">
                                            <!--<![endif]-->
                                            <table role="presentation" width="100%" cellpadding="0" cellspacing="0"
                                                style="width:100% !important;">
                                                <tr>
                                                    <td>
                                                        <div class="t24"
                                                            style="mso-line-height-rule:exactly;mso-line-height-alt:20px;line-height:20px;font-size:1px;display:block;">
                                                            &nbsp;&nbsp;</div>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="center">
                                                        <table class="t26" role="presentation" cellpadding="0"
                                                            cellspacing="0" style="Margin-left:auto;Margin-right:auto;">
                                                            <tr>
                                                                <!--[if mso]>
<td width="506" class="t25">
<![endif]-->
                                                                <!--[if !mso]>-->
                                                                <td class="t25" style="width:506px;">
                                                                    <!--<![endif]-->
                                                                    <div style="font-size:0px;"><img class="t23"
                                                                            style="display:block;border:0;height:auto;width:100%;Margin:0;max-width:100%;"
                                                                            width="506" height="94.453125" alt=""
                                                                            src="cid:image" /></div>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>
                                                        <div class="t27"
                                                            style="mso-line-height-rule:exactly;mso-line-height-alt:7px;line-height:7px;font-size:1px;display:block;">
                                                            &nbsp;&nbsp;</div>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="center">
                                                        <table class="t30" role="presentation" cellpadding="0"
                                                            cellspacing="0" style="Margin-left:auto;Margin-right:auto;">
                                                            <tr>
                                                                <!--[if mso]>
<td width="600" class="t29">
<![endif]-->
                                                                <!--[if !mso]>-->
                                                                <td class="t29" style="width:600px;">
                                                                    <!--<![endif]-->
                                                                    <p class="t28"
                                                                        style="margin:0;Margin:0;font-family:Fira Sans,BlinkMacSystemFont,Segoe UI,Helvetica Neue,Arial,sans-serif;line-height:26px;font-weight:400;font-style:normal;font-size:12px;text-decoration:none;text-transform:none;direction:ltr;color:#9095A2;text-align:center;mso-line-height-rule:exactly;mso-text-raise:4px;">
                                                                        If you did not issue this operation, you can
                                                                        safely delete this email.</p>
                                                                </td>
                                                           </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="center">
                                                        <table class="t33" role="presentation" cellpadding="0"
                                                            cellspacing="0" style="Margin-left:auto;Margin-right:auto;">
                                                            <tr>
                                                                <!--[if mso]>
<td width="600" class="t32">
<![endif]-->
                                                                <!--[if !mso]>-->
                                                                <td class="t32" style="width:600px;">
                                                                    <!--<![endif]-->
                                                                    <p class="t31"
                                                                        style="margin:0;Margin:0;font-family:Fira Sans,BlinkMacSystemFont,Segoe UI,Helvetica Neue,Arial,sans-serif;line-height:26px;font-weight:400;font-style:normal;font-size:12px;text-decoration:none;text-transform:none;direction:ltr;color:#9095A2;text-align:center;mso-line-height-rule:exactly;mso-text-raise:4px;">
                                                                        Form-iit. All rights reserved. Copyright 2024
                                                                    </p>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>
                                                        <div class="t34"
                                                            style="mso-line-height-rule:exactly;mso-line-height-alt:8px;line-height:8px;font-size:1px;display:block;">
                                                            &nbsp;&nbsp;</div>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
    <div class="gmail-mobile-forced-width" style="white-space: nowrap; font: 15px courier; line-height: 0;">&nbsp;
        &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
        &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
    </div>
</body>

</html>
""";
  }
}
