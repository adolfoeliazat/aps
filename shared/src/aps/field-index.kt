package aps

object fields                                              : Fuckers<FieldSpec>(null) {
    val genericFirstName                                   by namedFucker {TextFieldSpec(it, t("TOTE", "Имя"), TextFieldType.STRING, minLen = 1, maxLen = 50)}
    val genericLastName                                    by namedFucker {TextFieldSpec(it, t("TOTE", "Фамилия"), TextFieldType.STRING, minLen = 1, maxLen = 50)}
    val genericEmail                                       by namedFucker {TextFieldSpec(it, t("TOTE", "Почта"), TextFieldType.EMAIL, minLen = 3, maxLen = 50)}
    val genericPassword                                    by namedFucker {TextFieldSpec(it, t("TOTE", "Пароль"), TextFieldType.PASSWORD, minLen = 8, maxLen = 50)}
    val genericPhone                                       by namedFucker {TextFieldSpec(it, t("TOTE", "Телефон"), TextFieldType.PHONE, minLen = 6, maxLen = 50, minDigits = 6)}
    val genericRejectionReason                             by namedFucker {TextFieldSpec(it, t("TOTE", "Причина отказа"), TextFieldType.TEXTAREA, 10, 5000)}

    val signUpFirstName                                    by namedFucker {genericFirstName.copy(name = it)}; val signUpFirstName_testRef = TestRef(signUpFirstName)
    val signUpLastName                                     by namedFucker {genericLastName.copy(name = it)}; val signUpLastName_testRef = TestRef(signUpLastName)
    val signUpEmail                                        by namedFucker {genericEmail.copy(name = it)}; val signUpEmail_testRef = TestRef(signUpEmail)

    val signInPassword                                     by namedFucker {genericPassword.copy(name = it, minLen = 0, maxLen = Int.MAX_VALUE)}; val signInPassword_testRef = TestRef(signInPassword)
    val signInEmail                                        by namedFucker {genericEmail.copy(name = it, minLen = 0, maxLen = Int.MAX_VALUE)}; val signInEmail_testRef = TestRef(signInEmail)

    val profilePhone                                       by namedFucker {genericPhone.copy(name = it)}; val profilePhone_testRef = TestRef(profilePhone)
    val aboutMe                                            by namedFucker {TextFieldSpec(it, t("TOTE", "Пара ласковых о себе"), TextFieldType.TEXTAREA, minLen = 1, maxLen = 300)}; val aboutMe_testRef = TestRef(aboutMe)

    val documentTitle                                      by namedFucker {TextFieldSpec(it, t("TOTE", "Тема работы"), TextFieldType.STRING, minLen = 3, maxLen = 100)}; val documentTitle_testRef = TestRef(documentTitle)
    val orderCustomerFirstName                             by namedFucker {genericFirstName.copy(name = it, title = t("TOTE", "Контактное имя"))}; val orderCustomerFirstName_testRef = TestRef(orderCustomerFirstName)
    val orderCustomerLastName                              by namedFucker {genericLastName.copy(name = it, title = t("TOTE", "Фамилия"), minLen = 0, optionalHint = true)}; val orderCustomerLastName_testRef = TestRef(orderCustomerLastName)
    val orderCustomerEmail                                 by namedFucker {genericEmail.copy(name = it)}; val orderCustomerEmail_testRef = TestRef(orderCustomerEmail)
    val orderCustomerPhone                                 by namedFucker {genericPhone.copy(name = it)}; val orderCustomerPhone_testRef = TestRef(orderCustomerPhone)
    val password                                           by namedFucker {TextFieldSpec(it, t("TOTE", "Пароль"), TextFieldType.PASSWORD, minLen = 6, maxLen = 50)}; val password_testRef = TestRef(password)
    val banReason                                          by namedFucker {TextFieldSpec(it, t("TOTE", "Причина бана"), TextFieldType.TEXTAREA, 0, 5000)}; val banReason_testRef = TestRef(banReason)
    val adminNotes                                         by namedFucker {TextFieldSpec(it, t("TOTE", "Админские заметки"), TextFieldType.TEXTAREA, 0, 5000)}; val adminNotes_testRef = TestRef(adminNotes)
    val searchString                                       by namedFucker {TextFieldSpec(it, "", TextFieldType.STRING, 0, 50)}; val searchString_testRef = TestRef(searchString)
    val orderDetails                                       by namedFucker {TextFieldSpec(it, t("TOTE", "Детали работы (задание)"), TextFieldType.TEXTAREA, minLen = 3, maxLen = 2000)}; val orderDetails_testRef = TestRef(orderDetails)
    val agreeTerms                                         by namedFucker {CheckboxFieldSpec(it, t("TOTE", "boobs"), mandatoryYesError = t("TOTE", "Необходимо принять соглашение"))}; val agreeTerms_testRef = TestRef(agreeTerms)
    val userState                                          by namedFucker {SelectFieldSpec(it, t("TOTE", "Статус"), UserState.values())}; val userState_testRef = TestRef(userState)
    val numPages                                           by namedFucker {IntFieldSpec(it, t("TOTE", "Количество страниц"), min = 1, max = 500)}; val numPages_testRef = TestRef(numPages)
    val numSources                                         by namedFucker {IntFieldSpec(it, t("TOTE", "Количество источников"), min = 0, max = 50)}; val numSources_testRef = TestRef(numSources)
    val fileFile_create                                    by namedFucker {FileFieldSpec(it, t("TOTE", "Файл"), allowedValueKinds = setOf(FileFieldValueKind.PROVIDED))}; val fileFile_create_testRef = TestRef(fileFile_create)
    val fileFile_update                                    by namedFucker {fileFile_create.copy(name = it, allowedValueKinds = setOf(FileFieldValueKind.UNCHANGED, FileFieldValueKind.PROVIDED))}; val fileFile_update_testRef = TestRef(fileFile_update)
    val fileTitle                                          by namedFucker {TextFieldSpec(it, t("TOTE", "Название"), TextFieldType.STRING, 3, 100)}; val fileTitle_testRef = TestRef(fileTitle)
    val fileDetails                                        by namedFucker {TextFieldSpec(it, t("TOTE", "Детали"), TextFieldType.TEXTAREA, 3, 2000)}; val fileDetails_testRef = TestRef(fileDetails)
    val uaDocumentType                                     by namedFucker {SelectFieldSpec(it, t("TOTE", "Тип документа"), UADocumentType.values())}; val uaDocumentType_testRef = TestRef(uaDocumentType)
    val rejectionReason                                    by namedFucker {genericRejectionReason.copy(name = it)}; val rejectionReason_testRef = TestRef(rejectionReason)

    val minAllowedPriceOffer                               by namedFucker {IntFieldSpec(it, t("TOTE", "Нижний предел стоимости"), type = IntFieldType.Money(), min = 100_00, max = 50000_00)}; val minAllowedPriceOffer_testRef = TestRef(minAllowedPriceOffer)
    val maxAllowedPriceOffer                               by namedFucker {IntFieldSpec(it, t("TOTE", "Верхний предел стоимости"), type = IntFieldType.Money(), min = 100_00, max = 50000_00)}; val maxAllowedPriceOffer_testRef = TestRef(maxAllowedPriceOffer)
    val minAllowedDurationOffer                            by namedFucker {IntFieldSpec(it, t("TOTE", "Нижний предел срока"), type = IntFieldType.Duration(), min = 1 * 24, max = 365 * 24)}; val minAllowedDurationOffer_testRef = TestRef(minAllowedDurationOffer)
    val maxAllowedDurationOffer                            by namedFucker {IntFieldSpec(it, t("TOTE", "Верхний предел срока"), type = IntFieldType.Duration(), min = 1 * 24, max = 365 * 24)}; val maxAllowedDurationOffer_testRef = TestRef(maxAllowedDurationOffer)

    val uaDocumentCategory                                 by namedFucker {DocumentCategoryFieldSpec(it, t("TOTE", "Категория"))}; val uaDocumentCategory_testRef = TestRef(uaDocumentCategory)
    val categorySubscriptions                              by namedFucker {DocumentCategorySetFieldSpec(it, t("TOTE", "Меня интересуют категории"))}; val categorySubscriptions_testRef = TestRef(categorySubscriptions)
}














