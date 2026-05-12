package com.github.ChelovekVreditel.chinese_cars.utils;

import com.github.ChelovekVreditel.chinese_cars.dtos.CarWithImageSource;
import com.github.ChelovekVreditel.chinese_cars.dtos.ConfigurationDetails;
import com.github.ChelovekVreditel.chinese_cars.enums.CarBrand;
import com.github.ChelovekVreditel.chinese_cars.models.Car;
import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class AudiParser {

    private final String BASE = "http://audi.cn";

    public List<CarWithImageSource> extractCarsModels(
        String externalUrlModels,
        String externalUrlModelsSpecificPart,
        String externalUrlBase
    ) throws IOException {

        // Создание браузера для получения HTML кода страницы после добавления всех данных
        try(Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            context.addCookies(Arrays.asList(
                new Cookie("cookiePolicy", "true")
                    .setDomain(".audi.cn")
                    .setPath("/")
            ));
            Page page = context.newPage();
            page.navigate(externalUrlModels, new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(240000)
            );
            page.waitForSelector(".btn.view-detail", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.ATTACHED)
            );
            String html = page.content();
            Document doc = Jsoup.parse(html);

            List<CarWithImageSource> extractedCarsModels = new ArrayList<>();

            Elements divsSeries = doc.getElementsByClass("series-block");
            for (Element series : divsSeries) {
                String seriesTitle = series.select(".series-title").text();
                // Пропуск серий кроме A и Q, так как в них повторяются модели
                if (!(seriesTitle.contains("A") || seriesTitle.contains("Q"))) continue;
                Elements divsModels = series.getElementsByClass("model-car");
                for (Element model : divsModels) {
                    String modelName = model.select(".model-name").text();
                    String rawPrice = Objects.requireNonNull(model.select(".model-price").first()).ownText();
                    BigDecimal modelBasePrice = new BigDecimal(rawPrice.replace(",", ""));
                    String sourceUrl = model.select(".btn.view-detail").attr("href")
                        .replace(externalUrlModelsSpecificPart, "");
                    sourceUrl = externalUrlBase + sourceUrl.replace("/", "@") + "?" +
                        sourceUrl.substring(sourceUrl.lastIndexOf("/") + 1).replace(".html", "");
                    Car rawCar = Car.builder()
                        .brand(CarBrand.Audi)
                        .series(seriesTitle)
                        .originalModel(modelName)
                        .basePriceCny(modelBasePrice)
                        .sourceUrl(sourceUrl)
                        .build();
                    String imgUrl = BASE + model.select(".model-image > img").first().attr("src");
                    extractedCarsModels.add(new CarWithImageSource(rawCar, imgUrl));
                }
            }
            browser.close();
            playwright.close();
            return extractedCarsModels;
        }
    }

    public List<ConfigurationDetails> extractConfigurationsDetails(
            String externalSourceUrl
    ) throws IOException {

        // Создание браузера для получения HTML кода страницы после добавления всех данных
        try(Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            context.addCookies(Arrays.asList(
                new Cookie("cookiePolicy", "true")
                    .setDomain(".audi.cn")
                    .setPath("/")
            ));
            Page page = context.newPage();
            page.navigate(externalSourceUrl, new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(240000)
            );
            page.waitForSelector(".value-cub", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.ATTACHED)
            );
            String html = page.content();
            Document doc = Jsoup.parse(html);

            List<ConfigurationDetails> result = new ArrayList<>();

            // Получаем перечень конфигураций модели
            Elements configurationElements = Objects.requireNonNull(doc.getElementsByClass("top-table_container").first())
                .select("td[data-key]");
            for (Element configuration : configurationElements) {
                String name = configuration.select(".car-name-title").text();
                BigDecimal basePriceCny = new BigDecimal(
                    Objects.requireNonNull(configuration.select(".price-name").first())
                        .ownText()
                        .replace(",", "")
                );
                CarConfiguration carConfiguration = CarConfiguration.builder()
                    .originalName(name)
                    .basePriceCny(basePriceCny)
                    .build();
                ConfigurationDetails configurationDetails = ConfigurationDetails.builder()
                    .carConfiguration(carConfiguration)
                    .build();
                result.add(configurationDetails);
            }

            // Получаем конфигурационные опции
            // Парсим по категориям
            Elements categoriesTables = doc.select("table[id~=compare_type_\\d+]");
            for (Element categoryTable : categoriesTables) {
                String category = Objects.requireNonNull(categoryTable.select(".type-name")
                    .first())
                    .child(0)
                    .text();
                Element optionsTable = categoryTable.nextElementSibling();
                assert optionsTable != null;
                Elements rows = optionsTable.select("tbody > tr");
                for (Element row : rows) {
                    Elements cells = row.select("td");
                    String name = cells.getFirst().select("div > div").text();
                    int cellsNum = cells.size();
                    for (int i = 1; i < cellsNum - 1; i++) {
                        String value;
                        Elements optionsType = cells.get(i).select(".options-type");
                        if (!optionsType.isEmpty()) {
                            Element type = Objects.requireNonNull(optionsType.first())
                                .select(".options-type-icon")
                                .first();
                            assert type != null;
                            if (type.hasClass("standard-icon")) value = "included";
                            else if (type.hasClass("alternative-icon")) value = "is_optional";
                            else value = "none";
                        }
                        else {
                            value = Objects.requireNonNull(cells.get(i).select(".value-cub > .item").first())
                                .child(0)
                                .text();
                        }

                        ConfigurationOption option = ConfigurationOption.builder()
                            .category(category)
                            .originalName(name)
                            .value(value)
                            .build();
                        if (value.equals("is_optional")) {
                            String raw = Objects.requireNonNull(cells.get(i).select(".value-cub > .item").first())
                                .child(0)
                                .text();
                            option.setPriceCny(new BigDecimal(raw.replaceAll("[^0-9.-]", "")));
                        }

                        result.get(i-1).getConfigurationOptions().add(option);
                    }
                }
            }

            browser.close();
            playwright.close();
            return result;
        }
    }
}
