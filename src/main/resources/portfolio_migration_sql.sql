-- Portfolio Management System - PostgreSQL Database Migration
-- This script creates the database schema and migrates data from db.json

-- Enable UUID extension for generating UUIDs if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Drop tables if they exist (in correct order to handle foreign keys)
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS holdings CASCADE;
DROP TABLE IF EXISTS watchlist CASCADE;
DROP TABLE IF EXISTS portfolios CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS brokers CASCADE;

-- Create Users table
CREATE TABLE users (
    id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create Brokers table
CREATE TABLE brokers (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL CHECK (type IN ('discount', 'full-service', 'professional', 'commission-free')),
    country VARCHAR(50) NOT NULL CHECK (country IN ('Canada', 'USA')),
    supported_currencies TEXT[] NOT NULL,
    trading_fee DECIMAL(10,2) NOT NULL DEFAULT 0,
    min_trading_fee DECIMAL(10,2) NOT NULL DEFAULT 0,
    max_trading_fee DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create Portfolios table
CREATE TABLE portfolios (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    user_id VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    broker VARCHAR(100) NOT NULL,
    currency VARCHAR(3) NOT NULL CHECK (currency IN ('USD', 'CAD')),
    total_value DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_cost DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_gain_loss DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_gain_loss_percent DECIMAL(8,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Composite unique constraint for user and portfolio name
    CONSTRAINT uk_user_portfolio_name UNIQUE (user_id, name)
);

-- Create Holdings table
CREATE TABLE holdings (
    id VARCHAR(50) PRIMARY KEY,
    portfolio_id VARCHAR(50) NOT NULL REFERENCES portfolios(id) ON DELETE CASCADE,
    symbol VARCHAR(20) NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('stock', 'etf')),
    market VARCHAR(50) NOT NULL,
    currency VARCHAR(3) NOT NULL CHECK (currency IN ('USD', 'CAD')),
    quantity DECIMAL(15,6) NOT NULL CHECK (quantity >= 0),
    average_price DECIMAL(15,4) NOT NULL CHECK (average_price >= 0),
    current_price DECIMAL(15,4) NOT NULL CHECK (current_price >= 0),
    total_cost DECIMAL(15,2) NOT NULL DEFAULT 0,
    current_value DECIMAL(15,2) NOT NULL DEFAULT 0,
    gain_loss DECIMAL(15,2) NOT NULL DEFAULT 0,
    gain_loss_percent DECIMAL(8,4) NOT NULL DEFAULT 0,
    sector VARCHAR(100),
    purchase_date TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Composite unique constraint for portfolio and symbol
    CONSTRAINT uk_portfolio_symbol UNIQUE (portfolio_id, symbol)
);

-- Create Transactions table
CREATE TABLE transactions (
    id VARCHAR(50) PRIMARY KEY,
    portfolio_id VARCHAR(50) NOT NULL REFERENCES portfolios(id) ON DELETE CASCADE,
    holding_id VARCHAR(50) REFERENCES holdings(id) ON DELETE SET NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('buy', 'sell', 'dividend')),
    symbol VARCHAR(20) NOT NULL,
    quantity DECIMAL(15,6) NOT NULL,
    price DECIMAL(15,4) NOT NULL CHECK (price >= 0),
    total_amount DECIMAL(15,2) NOT NULL,
    fees DECIMAL(15,2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL CHECK (currency IN ('USD', 'CAD')),
    transaction_date TIMESTAMP WITH TIME ZONE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create Watchlist table
CREATE TABLE watchlist (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    symbol VARCHAR(20) NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    current_price DECIMAL(15,4) NOT NULL DEFAULT 0,
    change_percent DECIMAL(8,4) NOT NULL DEFAULT 0,
    added_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Composite unique constraint for user and symbol
    CONSTRAINT uk_user_watchlist_symbol UNIQUE (user_id, symbol)
);

-- Create indexes for better performance
CREATE INDEX idx_portfolios_user_id ON portfolios(user_id);
CREATE INDEX idx_portfolios_broker ON portfolios(broker);
CREATE INDEX idx_portfolios_currency ON portfolios(currency);
CREATE INDEX idx_portfolios_created_at ON portfolios(created_at);
CREATE INDEX idx_portfolios_updated_at ON portfolios(updated_at);

CREATE INDEX idx_holdings_portfolio_id ON holdings(portfolio_id);
CREATE INDEX idx_holdings_symbol ON holdings(symbol);
CREATE INDEX idx_holdings_type ON holdings(type);
CREATE INDEX idx_holdings_sector ON holdings(sector);
CREATE INDEX idx_holdings_last_updated ON holdings(last_updated);

CREATE INDEX idx_transactions_portfolio_id ON transactions(portfolio_id);
CREATE INDEX idx_transactions_holding_id ON transactions(holding_id);
CREATE INDEX idx_transactions_type ON transactions(type);
CREATE INDEX idx_transactions_symbol ON transactions(symbol);
CREATE INDEX idx_transactions_transaction_date ON transactions(transaction_date);

CREATE INDEX idx_watchlist_user_id ON watchlist(user_id);
CREATE INDEX idx_watchlist_symbol ON watchlist(symbol);
CREATE INDEX idx_watchlist_added_date ON watchlist(added_date);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Insert Users data
INSERT INTO users (id, username, password, email, first_name, last_name, created_at, last_login_at, is_active) VALUES
('1', 'admin', 'admin123', 'admin@portfolio.com', 'Firstname', 'lastname', '2024-01-15T10:30:00.000Z', '2025-09-16T17:20:44.192Z', true),
('2', 'johndoe', 'password123', 'john.doe@email.com', 'John', 'Doe', '2024-02-01T14:20:00.000Z', '2024-08-25T16:45:00.000Z', true),
('3', 'investor', 'invest2024', 'investor@portfolio.com', 'Smart', 'Investor', '2024-03-10T09:00:00.000Z', '2024-08-24T12:30:00.000Z', true);

-- Insert Brokers data
INSERT INTO brokers (id, name, type, country, supported_currencies, trading_fee, min_trading_fee, max_trading_fee) VALUES
('1', 'Questrade', 'discount', 'Canada', ARRAY['CAD', 'USD'], 4.95, 4.95, 9.95),
('2', 'TD Direct Investing', 'full-service', 'Canada', ARRAY['CAD', 'USD'], 9.99, 9.99, 9.99),
('3', 'Interactive Brokers', 'professional', 'USA', ARRAY['USD', 'CAD', 'EUR', 'GBP'], 0.005, 1, 1),
('4', 'Wealthsimple Trade', 'commission-free', 'Canada', ARRAY['CAD'], 0, 0, 0);

-- Insert Portfolios data
INSERT INTO portfolios (id, name, description, user_id, broker, currency, total_value, total_cost, total_gain_loss, total_gain_loss_percent, created_at, updated_at, is_active) VALUES
('1', 'Growth Portfolio', 'Focused on growth stocks and technology ETFs', '1', 'Questrade', 'USD', 125480.75, 98250, 27230.75, 27.71, '2024-01-20T10:00:00.000Z', '2024-08-26T09:30:00.000Z', true),
('2', 'Dividend Income', 'Conservative dividend-paying stocks and REITs', '1', 'TD Direct Investing', 'CAD', 89750.25, 85000, 4750.25, 5.59, '2024-02-15T14:30:00.000Z', '2024-08-26T09:30:00.000Z', true),
('3', 'Tech Innovation', 'High-growth technology stocks and disruptive companies', '2', 'Interactive Brokers', 'USD', 67890.5, 75000, -7109.5, -9.48, '2024-03-01T11:15:00.000Z', '2024-08-26T09:30:00.000Z', true),
('c9bf', 'asdf', 'asdfasdfas', '1', 'Questrade', 'USD', 0, 0, 0, 0, '2025-09-06T14:45:33.169Z', '2025-09-06T14:45:33.169Z', true),
('5136', 'zxcv', 'zxcvbzxcvb', '1', 'TD Direct Investing', 'USD', 0, 0, 0, 0, '2025-09-06T23:19:46.648Z', '2025-09-06T23:19:46.648Z', true);

-- Insert Holdings data
INSERT INTO holdings (id, portfolio_id, symbol, company_name, type, market, currency, quantity, average_price, current_price, total_cost, current_value, gain_loss, gain_loss_percent, sector, purchase_date, last_updated) VALUES
('1', '1', 'AAPL', 'Apple Inc.', 'stock', 'NASDAQ', 'USD', 150, 180.25, 234.5, 27037.5, 35175, 8137.5, 30.11, 'Technology', '2024-01-25T00:00:00.000Z', '2024-08-26T16:00:00.000Z'),
('2', '1', 'GOOGL', 'Alphabet Inc. Class A', 'stock', 'NASDAQ', 'USD', 80, 142.75, 168.25, 11420, 13460, 2040, 17.87, 'Technology', '2024-02-10T00:00:00.000Z', '2024-08-26T16:00:00.000Z'),
('3', '1', 'VTI', 'Vanguard Total Stock Market ETF', 'etf', 'NYSE', 'USD', 200, 245.8, 262.15, 49160, 52430, 3270, 6.66, 'Diversified', '2024-01-30T00:00:00.000Z', '2024-08-26T16:00:00.000Z'),
('4', '2', 'RY.TO', 'Royal Bank of Canada', 'stock', 'TSX', 'CAD', 100, 125.5, 132.75, 12550, 13275, 725, 5.78, 'Financial Services', '2024-02-20T00:00:00.000Z', '2024-08-26T16:00:00.000Z'),
('5', '2', 'VCN.TO', 'Vanguard FTSE Canada All Cap Index ETF', 'etf', 'TSX', 'CAD', 300, 45.2, 47.85, 13560, 14355, 795, 5.86, 'Diversified', '2024-03-01T00:00:00.000Z', '2024-08-26T16:00:00.000Z'),
('6', '3', 'TSLA', 'Tesla, Inc.', 'stock', 'NASDAQ', 'USD', 50, 280, 245.5, 14000, 12275, -1725, -12.32, 'Consumer Cyclical', '2024-03-15T00:00:00.000Z', '2024-08-26T16:00:00.000Z'),
('7', '3', 'NVDA', 'NVIDIA Corporation', 'stock', 'NASDAQ', 'USD', 30, 450, 520.75, 13500, 15622.5, 2122.5, 15.72, 'Technology', '2024-04-01T00:00:00.000Z', '2024-08-26T16:00:00.000Z'),
('b4f3', 'c9bf', 'TSLA1', 'TSLA', 'stock', 'NASDAQ', 'USD', 1, 100, 200, 100, 200, 100, 100, 'Technology', '2025-09-16T17:51:37.333Z', '2025-09-16T17:59:50.982Z');

-- Insert Transactions data
INSERT INTO transactions (id, portfolio_id, holding_id, type, symbol, quantity, price, total_amount, fees, currency, transaction_date, notes) VALUES
('1', '1', '1', 'buy', 'AAPL', 100, 175.5, 17550, 9.95, 'USD', '2024-01-25T10:30:00.000Z', 'Initial purchase'),
('2', '1', '1', 'buy', 'AAPL', 50, 190, 9500, 9.95, 'USD', '2024-03-15T14:20:00.000Z', 'Additional shares'),
('3', '1', '2', 'buy', 'GOOGL', 80, 142.75, 11420, 9.95, 'USD', '2024-02-10T11:45:00.000Z', 'New position'),
('4', '2', '4', 'buy', 'RY.TO', 100, 125.5, 12550, 9.99, 'CAD', '2024-02-20T09:15:00.000Z', 'Canadian dividend stock');

-- Insert Watchlist data
INSERT INTO watchlist (id, user_id, symbol, company_name, current_price, change_percent, added_date) VALUES
('1', '1', 'MSFT', 'Microsoft Corporation', 425.5, 1.25, '2024-08-01T00:00:00.000Z'),
('2', '1', 'AMZN', 'Amazon.com, Inc.', 178.25, -0.85, '2024-07-15T00:00:00.000Z'),
('3', '2', 'META', 'Meta Platforms, Inc.', 512.75, 2.15, '2024-08-10T00:00:00.000Z');

-- Create triggers to automatically update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers to relevant tables
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_portfolios_updated_at BEFORE UPDATE ON portfolios FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_holdings_updated_at BEFORE UPDATE ON holdings FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_watchlist_updated_at BEFORE UPDATE ON watchlist FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_brokers_updated_at BEFORE UPDATE ON brokers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create functions to automatically calculate portfolio totals when holdings change
CREATE OR REPLACE FUNCTION update_portfolio_totals()
RETURNS TRIGGER AS $$
DECLARE
    portfolio_record RECORD;
BEGIN
    -- Get the portfolio ID from the changed holding
    IF TG_OP = 'DELETE' THEN
        portfolio_record.id := OLD.portfolio_id;
    ELSE
        portfolio_record.id := NEW.portfolio_id;
    END IF;

    -- Update portfolio totals based on current holdings
    UPDATE portfolios SET
        total_value = COALESCE((
            SELECT SUM(current_value) 
            FROM holdings 
            WHERE portfolio_id = portfolio_record.id
        ), 0),
        total_cost = COALESCE((
            SELECT SUM(total_cost) 
            FROM holdings 
            WHERE portfolio_id = portfolio_record.id
        ), 0),
        updated_at = CURRENT_TIMESTAMP
    WHERE id = portfolio_record.id;

    -- Update calculated fields
    UPDATE portfolios SET
        total_gain_loss = total_value - total_cost,
        total_gain_loss_percent = CASE 
            WHEN total_cost > 0 THEN ((total_value - total_cost) / total_cost) * 100 
            ELSE 0 
        END,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = portfolio_record.id;

    RETURN COALESCE(NEW, OLD);
END;
$$ language 'plpgsql';

-- Apply trigger to holdings table
CREATE TRIGGER trigger_update_portfolio_totals 
    AFTER INSERT OR UPDATE OR DELETE ON holdings 
    FOR EACH ROW EXECUTE FUNCTION update_portfolio_totals();

-- Create function to automatically update holding calculated fields
CREATE OR REPLACE FUNCTION update_holding_calculated_fields()
RETURNS TRIGGER AS $$
BEGIN
    -- Calculate derived values automatically
    NEW.total_cost = NEW.quantity * NEW.average_price;
    NEW.current_value = NEW.quantity * NEW.current_price;
    NEW.gain_loss = NEW.current_value - NEW.total_cost;
    NEW.gain_loss_percent = CASE 
        WHEN NEW.total_cost > 0 THEN (NEW.gain_loss / NEW.total_cost) * 100 
        ELSE 0 
    END;
    NEW.last_updated = CURRENT_TIMESTAMP;
    
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply trigger to holdings table
CREATE TRIGGER trigger_update_holding_calculated_fields 
    BEFORE INSERT OR UPDATE ON holdings 
    FOR EACH ROW EXECUTE FUNCTION update_holding_calculated_fields();

-- Grant permissions (adjust as needed for your application user)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO portfolio_app_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO portfolio_app_user;

-- Add some additional constraints for data integrity
ALTER TABLE transactions ADD CONSTRAINT check_transaction_amount 
    CHECK (total_amount >= 0 OR type = 'sell');

ALTER TABLE holdings ADD CONSTRAINT check_holding_values
    CHECK (quantity > 0 AND average_price > 0 AND current_price >= 0);

-- Create view for portfolio summary with holdings count
CREATE VIEW portfolio_summary AS
SELECT 
    p.*,
    COALESCE(h.holdings_count, 0) as holdings_count,
    COALESCE(h.last_holding_update, p.updated_at) as last_activity
FROM portfolios p
LEFT JOIN (
    SELECT 
        portfolio_id,
        COUNT(*) as holdings_count,
        MAX(last_updated) as last_holding_update
    FROM holdings
    GROUP BY portfolio_id
) h ON p.id = h.portfolio_id;

-- Create view for user portfolio analytics
CREATE VIEW user_portfolio_analytics AS
SELECT 
    u.id as user_id,
    u.username,
    u.first_name,
    u.last_name,
    COUNT(DISTINCT p.id) as portfolio_count,
    COUNT(DISTINCT h.id) as total_holdings_count,
    SUM(p.total_value) as total_portfolio_value,
    SUM(p.total_cost) as total_cost,
    SUM(p.total_gain_loss) as total_gain_loss,
    CASE 
        WHEN SUM(p.total_cost) > 0 THEN (SUM(p.total_gain_loss) / SUM(p.total_cost)) * 100 
        ELSE 0 
    END as overall_gain_loss_percent
FROM users u
LEFT JOIN portfolios p ON u.id = p.user_id AND p.is_active = true
LEFT JOIN holdings h ON p.id = h.portfolio_id
GROUP BY u.id, u.username, u.first_name, u.last_name;

-- Create view for sector allocation across all portfolios
CREATE VIEW sector_allocation_view AS
SELECT 
    p.user_id,
    p.id as portfolio_id,
    p.name as portfolio_name,
    h.sector,
    SUM(h.current_value) as sector_value,
    COUNT(h.id) as holdings_in_sector,
    (SUM(h.current_value) / p.total_value * 100) as sector_percentage_in_portfolio
FROM portfolios p
JOIN holdings h ON p.id = h.portfolio_id
WHERE p.is_active = true AND p.total_value > 0
GROUP BY p.user_id, p.id, p.name, p.total_value, h.sector
ORDER BY p.user_id, p.id, sector_value DESC;

-- Insert some sample additional data for testing (optional)
COMMENT ON TABLE users IS 'User accounts for the portfolio management system';
COMMENT ON TABLE portfolios IS 'Investment portfolios belonging to users';
COMMENT ON TABLE holdings IS 'Individual stock/ETF holdings within portfolios';
COMMENT ON TABLE transactions IS 'Transaction history for portfolio holdings';
COMMENT ON TABLE watchlist IS 'User watchlists for tracking potential investments';
COMMENT ON TABLE brokers IS 'Available brokerage firms and their fee structures';

-- Final verification queries (uncomment to run)
-- SELECT 'Users' as table_name, COUNT(*) as record_count FROM users
-- UNION ALL SELECT 'Brokers', COUNT(*) FROM brokers
-- UNION ALL SELECT 'Portfolios', COUNT(*) FROM portfolios  
-- UNION ALL SELECT 'Holdings', COUNT(*) FROM holdings
-- UNION ALL SELECT 'Transactions', COUNT(*) FROM transactions
-- UNION ALL SELECT 'Watchlist', COUNT(*) FROM watchlist;