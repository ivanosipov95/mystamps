/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.tests.cases;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.mystamps.web.Url;
import ru.mystamps.web.tests.page.AbstractPage;
import ru.mystamps.web.tests.page.AddSeriesPage;
import ru.mystamps.web.tests.page.InfoSeriesPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.fest.PageWithFormAssert.assertThat;
import static ru.mystamps.web.validation.ValidationRules.MAX_SERIES_COMMENT_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.MAX_STAMPS_IN_SERIES;
import static ru.mystamps.web.validation.ValidationRules.MIN_STAMPS_IN_SERIES;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * The main difference between this test and {@link WhenUserAddSeries} is that as admin we have
 * additional field for comment.
 */
public class WhenAdminAddSeries extends WhenAnyUserAtAnyPageWithForm<AddSeriesPage> {
	
	private static final int SINCE_YEAR     = 1840;
	private static final int CURRENT_YEAR   = new GregorianCalendar().get(Calendar.YEAR);
	
	private static final List<String> EXPECTED_YEARS =
		new ArrayList<>(CURRENT_YEAR - SINCE_YEAR + 1);
	
	private static final String SAMPLE_IMAGE_NAME = "test.png";
	private static final String EMPTY_IMAGE_NAME  = "empty.jpg";
	private static final String SAMPLE_IMAGE_PATH;
	private static final String EMPTY_IMAGE_PATH;
	
	static {
		EXPECTED_YEARS.add("Year");
		// years in reverse order
		for (int i = CURRENT_YEAR; i >= SINCE_YEAR; i--) {
			EXPECTED_YEARS.add(String.valueOf(i));
		}
		
		try {
			SAMPLE_IMAGE_PATH = new File(
				WhenAdminAddSeries.class.getClassLoader().getResource(SAMPLE_IMAGE_NAME).toURI()
			).getAbsolutePath();
			
			EMPTY_IMAGE_PATH = new File(
				WhenAdminAddSeries.class.getClassLoader().getResource(EMPTY_IMAGE_NAME).toURI()
			).getAbsolutePath();
		
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Value("${valid_admin_login}")
	private String validAdminLogin;
	
	@Value("${valid_admin_password}")
	private String validAdminPassword;
	
	@Value("${existing_michel_number}")
	private String existingMichelNumber;
	
	@Value("${existing_scott_number}")
	private String existingScottNumber;
	
	@Value("${existing_yvert_number}")
	private String existingYvertNumber;
	
	@Value("${existing_gibbons_number}")
	private String existingGibbonsNumber;
	
	@Value("${valid_category_name_en}")
	private String validCategoryName;
	
	public WhenAdminAddSeries() {
		super(AddSeriesPage.class);
		hasTitle(tr("t_add_series"));
		hasHeader(StringUtils.capitalize(tr("t_add_series")));
	}
	
	@BeforeClass
	public void login() {
		page.login(validAdminLogin, validAdminPassword);
	}
	
	@BeforeMethod
	public void openPage() {
		page.open();
	}
	
	@AfterClass(alwaysRun = true)
	public void tearDown() {
		page.logout();
	}
	
	@Test(groups = "std")
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test(groups = "valid", dependsOnGroups = "std", dataProvider = "validCatalogNumbers")
	public void michelNumbersShouldAcceptValidValues(String numbers, Object whatever) {
		page.showCatalogNumbers();
		page.fillMichelNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("michelNumbers").hasNoError();
	}
	
	@Test(groups = "valid", dependsOnGroups = "std", dataProvider = "validCatalogNumbers")
	public void scottNumbersShouldAcceptValidValues(String numbers, Object whatever) {
		page.showCatalogNumbers();
		page.fillScottNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("scottNumbers").hasNoError();
	}
	
	@Test(groups = "valid", dependsOnGroups = "std", dataProvider = "validCatalogNumbers")
	public void yvertNumbersShouldAcceptValidValues(String numbers, Object whatever) {
		page.showCatalogNumbers();
		page.fillYvertNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("yvertNumbers").hasNoError();
	}
	
	@Test(groups = "valid", dependsOnGroups = "std", dataProvider = "validCatalogNumbers")
	public void gibbonsNumbersShouldAcceptValidValues(String numbers, Object whatever) {
		page.showCatalogNumbers();
		page.fillGibbonsNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("gibbonsNumbers").hasNoError();
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void quantityShouldBeANumber() {
		page.fillQuantity("NaN");
		
		page.submit();
		
		assertThat(page).field("quantity").hasError(tr("typeMismatch"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void quantityShouldBeNotLessThanLimit() {
		page.fillQuantity(String.valueOf(MIN_STAMPS_IN_SERIES - 1));
		
		page.submit();
		
		assertThat(page)
			.field("quantity")
			.hasError(tr("javax.validation.constraints.Min.message", MIN_STAMPS_IN_SERIES));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void quantityShouldNotBeGreaterThanLimit() {
		page.fillQuantity(String.valueOf(MAX_STAMPS_IN_SERIES + 1));
		
		page.submit();
		
		assertThat(page)
			.field("quantity")
			.hasError(tr("javax.validation.constraints.Max.message", MAX_STAMPS_IN_SERIES));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogNumbers")
	public void michelNumbersShouldRejectInvalidValues(String numbers, String msg) {
		page.showCatalogNumbers();
		page.fillMichelNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("michelNumbers").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogNumbers")
	public void scottNumbersShouldRejectInvalidValues(String numbers, String msg) {
		page.showCatalogNumbers();
		page.fillScottNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("scottNumbers").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogNumbers")
	public void yvertNumbersShouldRejectInvalidValues(String numbers, String msg) {
		page.showCatalogNumbers();
		page.fillYvertNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("yvertNumbers").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogNumbers")
	public void gibbonsNumbersShouldRejectInvalidValues(String numbers, String msg) {
		page.showCatalogNumbers();
		page.fillGibbonsNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("gibbonsNumbers").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogPrices")
	public void michelPriceShouldRejectInvalidValues(String price, String msg) {
		page.showCatalogNumbers();
		page.fillMichelPrice(price);
		
		page.submit();
		
		assertThat(page).field("michelPrice").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogPrices")
	public void scottPriceShouldRejectInvalidValues(String price, String msg) {
		page.showCatalogNumbers();
		page.fillScottPrice(price);
		
		page.submit();
		
		assertThat(page).field("scottPrice").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogPrices")
	public void yvertPriceShouldRejectInvalidValues(String price, String msg) {
		page.showCatalogNumbers();
		page.fillYvertPrice(price);
		
		page.submit();
		
		assertThat(page).field("yvertPrice").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogPrices")
	public void gibbonsPriceShouldRejectInvalidValues(String price, String msg) {
		page.showCatalogNumbers();
		page.fillGibbonsPrice(price);
		
		page.submit();
		
		assertThat(page).field("gibbonsPrice").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void michelNumbersShouldRejectExistingValues() {
		page.showCatalogNumbers();
		page.fillMichelNumbers(existingMichelNumber);
		
		page.submit();
		
		assertThat(page)
				.field("michelNumbers")
				.hasError(tr("ru.mystamps.web.validation.jsr303.UniqueMichelNumbers.message"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void scottNumbersShouldRejectExistingValues() {
		page.showCatalogNumbers();
		page.fillScottNumbers(existingScottNumber);
		
		page.submit();
		
		assertThat(page)
				.field("scottNumbers")
				.hasError(tr("ru.mystamps.web.validation.jsr303.UniqueScottNumbers.message"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void yvertNumbersShouldRejectExistingValues() {
		page.showCatalogNumbers();
		page.fillYvertNumbers(existingYvertNumber);
		
		page.submit();
		
		assertThat(page)
				.field("yvertNumbers")
				.hasError(tr("ru.mystamps.web.validation.jsr303.UniqueYvertNumbers.message"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void gibbonsNumbersShouldRejectExistingValues() {
		page.showCatalogNumbers();
		page.fillGibbonsNumbers(existingGibbonsNumber);
		
		page.submit();
		
		assertThat(page)
				.field("gibbonsNumbers")
				.hasError(tr("ru.mystamps.web.validation.jsr303.UniqueGibbonsNumbers.message"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void commentShouldNotBeTooLong() {
		page.showComment();
		page.fillComment(StringUtils.repeat("x", MAX_SERIES_COMMENT_LENGTH + 1));
		
		page.submit();
		
		assertThat(page)
			.field("comment")
			.hasError(tr("value.too-long", MAX_SERIES_COMMENT_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void imageSizeMustBeGreaterThanZero() {
		page.fillImage(EMPTY_IMAGE_PATH);
		
		page.submit();
		
		assertThat(page)
			.field("image")
			.hasError(tr("ru.mystamps.web.validation.jsr303.NotEmptyFile.message"));
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void issueYearFieldShouldHaveOptionsForRangeFrom1840ToCurrentYear() {
		page.showDateOfRelease();
		
		assertThat(page.getYearFieldValues()).isEqualTo(EXPECTED_YEARS);
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void michelNumbersShouldBeStripedFromSpaces() {
		page.showCatalogNumbers();
		page.fillMichelNumbers(" 1 , 2 ");
		
		page.submit();
		
		assertThat(page).field("michelNumbers").hasValue("1,2");
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void scottNumbersShouldBeStripedFromSpaces() {
		page.showCatalogNumbers();
		page.fillScottNumbers(" 3 , 4 ");
		
		page.submit();
		
		assertThat(page).field("scottNumbers").hasValue("3,4");
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void yvertNumbersShouldBeStripedFromSpaces() {
		page.showCatalogNumbers();
		page.fillYvertNumbers(" 5 , 6 ");
		
		page.submit();
		
		assertThat(page).field("yvertNumbers").hasValue("5,6");
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void gibbonsNumbersShouldBeStripedFromSpaces() {
		page.showCatalogNumbers();
		page.fillGibbonsNumbers(" 7 , 8 ");
		
		page.submit();
		
		assertThat(page).field("gibbonsNumbers").hasValue("7,8");
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void commentShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.showComment();
		page.fillComment(" example comment ");
		
		page.submit();
		
		assertThat(page).field("comment").hasValue("example comment");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldCreateSeriesWithOnlyRequiredFieldsFilled() {
		String expectedCategoryName = validCategoryName;
		String expectedQuantity     = "2";
		String expectedPageUrl      = Url.INFO_SERIES_PAGE.replace("{id}", "\\d+");
		String expectedImageUrl     = Url.SITE + Url.GET_IMAGE_PAGE.replace("{id}", "\\d+");

		page.fillCategory(expectedCategoryName);
		page.fillQuantity(expectedQuantity);
		page.fillImage(SAMPLE_IMAGE_PATH);
		
		AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		InfoSeriesPage nextPage = (InfoSeriesPage)next;
		
		assertThat(nextPage.getCurrentUrl()).matches(expectedPageUrl);
		
		List<String> imageUrls = nextPage.getImageUrls();
		assertThat(imageUrls).hasSize(1);
		assertThat(imageUrls.get(0)).matches(expectedImageUrl);
		
		assertThat(nextPage.getCategory()).isEqualTo(expectedCategoryName);
		assertThat(nextPage.getQuantity()).isEqualTo(expectedQuantity);
		assertThat(nextPage.getPerforated()).isEqualTo(tr("t_yes"));
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldCreateSeriesWithAllFieldsFilled() {
		String expectedPageUrl      = Url.INFO_SERIES_PAGE.replace("{id}", "\\d+");
		String expectedImageUrl     = Url.SITE + Url.GET_IMAGE_PAGE.replace("{id}", "\\d+");
		String expectedQuantity     = "3";
		String day                  = "4";
		String month                = "5";
		String year                 = "1999";
		String expectedCountryName  = "Italy";
		String expectedCategoryName = validCategoryName;
		String expectedComment      = "Any text";

		page.fillCategory(expectedCategoryName);
		page.fillCountry(expectedCountryName);
		
		page.showDateOfRelease();
		page.fillDay(day);
		page.fillMonth(month);
		page.fillYear(year);
		
		page.fillQuantity(expectedQuantity);
		page.fillPerforated(false);
		
		page.showCatalogNumbers();
		
		page.fillMichelNumbers("101, 102, 103");
		page.fillMichelPrice("10.5");
		
		page.fillScottNumbers("110, 111, 112");
		page.fillScottPrice("1000");
		
		page.fillYvertNumbers("120, 121, 122");
		page.fillYvertPrice("8.11");
		
		page.fillGibbonsNumbers("130, 131, 132");
		page.fillGibbonsPrice("400.335");
		
		page.showComment();
		page.fillComment(expectedComment);
		
		page.fillImage(SAMPLE_IMAGE_PATH);
		
		AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		InfoSeriesPage nextPage = (InfoSeriesPage)next;
		
		assertThat(nextPage.getCurrentUrl()).matches(expectedPageUrl);
		
		List<String> imageUrls = nextPage.getImageUrls();
		assertThat(imageUrls).hasSize(1);
		assertThat(imageUrls.get(0)).matches(expectedImageUrl);

		assertThat(nextPage.getCategory()).isEqualTo(expectedCategoryName);
		assertThat(nextPage.getCountry()).isEqualTo(expectedCountryName);
		assertThat(nextPage.getDateOfRelease()).isEqualTo("04.05.1999");
		assertThat(nextPage.getQuantity()).isEqualTo(expectedQuantity);
		assertThat(nextPage.getPerforated()).isEqualTo(tr("t_no"));
		
		assertThat(nextPage.getMichelCatalogInfo()).isEqualTo("#101-103 (10.5 EUR)");
		assertThat(nextPage.getScottCatalogInfo()).isEqualTo("#110-112 (1000 USD)");
		assertThat(nextPage.getYvertCatalogInfo()).isEqualTo("#120-122 (8.11 EUR)");
		// TODO: disable rounding mode
		assertThat(nextPage.getGibbonsCatalogInfo()).isEqualTo("#130-132 (400.34 GBP)");
		
		assertThat(nextPage.getComment()).isEqualTo(expectedComment);
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldIgnoreDuplicatedMichelNumbers() {
		page.fillCategory(validCategoryName);
		page.fillQuantity("2");
		page.fillImage(SAMPLE_IMAGE_PATH);
		page.showCatalogNumbers();
		page.fillMichelNumbers("104,105,104");
		
		AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		InfoSeriesPage nextPage = (InfoSeriesPage)next;
		assertThat(nextPage.getMichelCatalogInfo()).isEqualTo("#104, 105");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldIgnoreDuplicatedScottNumbers() {
		page.fillCategory(validCategoryName);
		page.fillQuantity("2");
		page.fillImage(SAMPLE_IMAGE_PATH);
		page.showCatalogNumbers();
		page.fillScottNumbers("114,115,114");
		
		AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		InfoSeriesPage nextPage = (InfoSeriesPage)next;
		assertThat(nextPage.getScottCatalogInfo()).isEqualTo("#114, 115");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldIgnoreDuplicatedYvertNumbers() {
		page.fillCategory(validCategoryName);
		page.fillQuantity("2");
		page.fillImage(SAMPLE_IMAGE_PATH);
		page.showCatalogNumbers();
		page.fillYvertNumbers("124,125,124");
		
		AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		InfoSeriesPage nextPage = (InfoSeriesPage)next;
		assertThat(nextPage.getYvertCatalogInfo()).isEqualTo("#124, 125");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldIgnoreDuplicatedGibbonsNumbers() {
		page.fillCategory(validCategoryName);
		page.fillQuantity("2");
		page.fillImage(SAMPLE_IMAGE_PATH);
		page.showCatalogNumbers();
		page.fillGibbonsNumbers("134,135,134");
		
		AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		InfoSeriesPage nextPage = (InfoSeriesPage)next;
		assertThat(nextPage.getGibbonsCatalogInfo()).isEqualTo("#134, 135");
	}
	
	@DataProvider(name = "validCatalogNumbers")
	public Object[][] getValidCatalogNumbers() {
		return new Object[][] {
			{"7", null},
			{"7,8", null},
			{"71, 81, 91", null},
			{"1000", null}
		};
	}
	
	@DataProvider(name = "invalidCatalogNumbers")
	public Object[][] getInvalidCatalogNumbers() {
		String expectedErrorMessage =
			tr("ru.mystamps.web.validation.jsr303.CatalogNumbers.message");
		
		return new Object[][] {
			{"t", expectedErrorMessage},
			{"t,t", expectedErrorMessage},
			{",1", expectedErrorMessage},
			{"1,", expectedErrorMessage},
			{"1,,2", expectedErrorMessage},
			{"0", expectedErrorMessage},
			{"05", expectedErrorMessage},
			{"1,09", expectedErrorMessage},
			{"10000", expectedErrorMessage}
		};
	}
	
	@DataProvider(name = "invalidCatalogPrices")
	public Object[][] getInvalidCatalogPrices() {
		String expectedErrorMessage = tr("ru.mystamps.web.validation.jsr303.Price.message");
		
		return new Object[][] {
			{"0", expectedErrorMessage},
			{"-1", expectedErrorMessage},
			{"NaN", expectedErrorMessage}
		};
	}
	
	@Override
	protected void checkServerResponseCode() {
		// Ignore this check because server always returns 401 for anonymous user and our test suite
		// lack ability to check response code after authentication.
	}
	
	@Override
	protected void shouldHaveUserBar() {
		// Ignore this check because when user authenticated there is no links for login/register.
	}
	
}
