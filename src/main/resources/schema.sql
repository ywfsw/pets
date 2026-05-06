CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 添加健康事件状态字段：0-待处理, 1-已完成
ALTER TABLE health_events ADD COLUMN IF NOT EXISTS status INTEGER DEFAULT 0;

-- 添加宠物性别字段：male-公, female-母, 空值-未知
ALTER TABLE pets ADD COLUMN IF NOT EXISTS gender VARCHAR(10);

-- 添加宠物备注/简介字段
ALTER TABLE pets ADD COLUMN IF NOT EXISTS notes TEXT;

-- 喂养记录表
CREATE TABLE IF NOT EXISTS feeding_records (
    id SERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    feed_time TIMESTAMP WITH TIME ZONE NOT NULL,
    food_type VARCHAR(100),
    amount_grams INTEGER,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 洗澡美容记录表
CREATE TABLE IF NOT EXISTS bathing_records (
    id SERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    bath_time TIMESTAMP WITH TIME ZONE NOT NULL,
    service_type VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);