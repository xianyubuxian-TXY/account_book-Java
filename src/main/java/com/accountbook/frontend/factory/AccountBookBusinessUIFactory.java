package com.accountbook.frontend.factory;

import com.accountbook.frontend.component.BudgetPage;
import com.accountbook.frontend.component.HomePage;
import com.accountbook.frontend.component.PageComponent;
import com.accountbook.frontend.component.RecordPage;
import com.accountbook.frontend.component.SearchPage;
import com.accountbook.frontend.component.SettingPage;
import com.accountbook.frontend.component.StatisticPage;
import com.accountbook.frontend.component.VisualPage;

public class AccountBookBusinessUIFactory implements UIFactory{
        @Override
    public PageComponent createHomePage() {
        return new HomePage();
    }

    @Override
    public PageComponent createRecordPage() {
        return new RecordPage();
    }

    @Override
    public PageComponent createSearchPage() {
        return new SearchPage();
    }

    @Override
    public PageComponent createStatisticPage() {
        return new StatisticPage();
    }

    @Override
    public PageComponent createVisualPage() {
        return new VisualPage();
    }

    @Override
    public PageComponent createBudgetPage() {
        return new BudgetPage();
    }

    @Override
    public PageComponent createSettingPage() {
        return new SettingPage();
    }
}
